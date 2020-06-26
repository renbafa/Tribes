package players.mcts_new;

import core.actions.Action;
import core.game.GameState;
import players.heuristics.StateHeuristic;
import players.mcts.MCTSParams;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MCTS {

    private MCTSParams params;
    private int playerID;
    private Random m_rnd;
    private int fmCallsCount;

    // Prune Parameter
    private boolean prune_enabled = true;
    private boolean pruning_flexible = true;
    private int visitedLimit = 20;
    private int remainingNode = 5;
    private int remainingNodeMinimum = 5;
    private double remainingRatio = 0.1;
    private boolean prune_visitAll = false;

    // Rollout
    private boolean rollout_strategy = true;

//    Statistic Parameter
    private int numIters = 0;
    private int depth;
    private int numNodes = 1;

    private double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};

    private TreeNode root;
    private GameState rootState;
    private StateHeuristic rootStateHeuristic;

    public MCTS(MCTSParams params, int playerID, GameState rootState, Random m_rnd, ArrayList<Integer> allIDs, ArrayList<Action> actions) {
        this.params = params;
        this.playerID = playerID;
        this.rootState = rootState;
        this.m_rnd = m_rnd;
        this.rootStateHeuristic = params.getHeuristic(playerID, allIDs);
        root = new TreeNode(actions, rootState.copy(), playerID, params);

    }

    public void search(ElapsedCpuTimer ect){

        double avgTimeTaken;
        double acumTimeTaken = 0;
        long remaining = 0;
        numIters = 0;

        int remainingLimit = 5;
        boolean stop = false;

        while(!stop){

            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            TreeNode selected = treePolicy();
            double delta = rollOut(selected);
            backUp(selected, delta);
            numIters++;

            //Stopping condition
            if(params.stop_type == params.STOP_TIME) {
                acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
                avgTimeTaken  = acumTimeTaken/numIters;
                remaining = ect.remainingTimeMillis();
                stop = remaining <= 2 * avgTimeTaken || remaining <= remainingLimit;
            }else if(params.stop_type == params.STOP_ITERATIONS) {
                stop = numIters >= params.num_iterations;
            }else if(params.stop_type == params.STOP_FMCALLS)
            {
                stop = fmCallsCount > params.num_fmcalls;
            }
        }
    }

    public Action mostVisitedAction(){

        TreeNode mostVisitedNode = root.mostVisitedTreeNode(bounds, m_rnd);
        report(mostVisitedNode);

        return mostVisitedNode.getAction();
    }

    private TreeNode treePolicy() {

        TreeNode currentNode = root;

        while (!currentNode.getGameState().isGameOver() && currentNode.getDepth() < params.ROLLOUT_LENGTH) {

            // Pruning
            if (prune_visitAll) {
                if (prune_enabled && !currentNode.isIs_prune() && currentNode.getUnexploredNodes().size() == 0 && currentNode.getnVisits() > visitedLimit) {
                    currentNode.sort();
                    if (pruning_flexible) {
                        remainingNode = (int) (currentNode.totalAction() * remainingRatio);
                        remainingNode = remainingNode > 5 ? remainingNode : remainingNodeMinimum;
                    }
                    currentNode.prune(remainingNode);
                    currentNode.setIs_prune(true);
                }
            }else{
                if (prune_enabled && !currentNode.isIs_prune() && currentNode.getnVisits() > visitedLimit) {
                    currentNode.sort();
                    if (pruning_flexible) {
                        remainingNode = (int) (currentNode.totalAction() * remainingRatio);
                        remainingNode = remainingNode > 5 ? remainingNode : remainingNodeMinimum;
                    }
                    currentNode.prune(remainingNode);
                    currentNode.setIs_prune(true);
                }
            }

            if (currentNode.isExpandable()) {
                return expand(currentNode);
            } else {
                currentNode = uct(currentNode);
            }
        }

        return currentNode;
    }

    private TreeNode expand(TreeNode currentNode){
        if (currentNode.totalAction() == 0){
            int newChildrenNum = currentNode.action2Node();
            fmCallsCount += newChildrenNum;
            numNodes += newChildrenNum;
        }

        double bestValue = -1;
        int bestIndex = 0;

        for (int i = 0; i < currentNode.getUnexploredNodes().size(); i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue) {
                bestValue = x;
                bestIndex = i;
            }
        }

        TreeNode exploredNode = currentNode.explore(bestIndex);

        if (exploredNode.getDepth() > depth){
            depth = exploredNode.getDepth();
        }

        return exploredNode;
    }

    private TreeNode uct(TreeNode currentNode){

        TreeNode bestNode = currentNode.getExploredNodes().get(0);
        if (currentNode.isMyTurn()){
            for (TreeNode t : currentNode.getExploredNodes()){
                if (t.uctValue(bounds, m_rnd) > bestNode.uctValue(bounds, m_rnd)){
                    bestNode = t;
                }
            }
        }else{
            for (TreeNode t : currentNode.getExploredNodes()){
                if (t.uctValue(bounds, m_rnd) < bestNode.uctValue(bounds, m_rnd)){
                    bestNode = t;
                }
            }
        }

        fmCallsCount++;

        return bestNode;
    }

    private double rollOut(TreeNode currentNode){
        if (params.ROLOUTS_ENABLED) {
            GameState simulation_state = currentNode.getGameState().copy();
            for (int i = currentNode.getDepth(); i < params.ROLLOUT_LENGTH; i++) {
                if (!simulation_state.isGameOver()) {
                    ArrayList<Action> allAvailableActions = simulation_state.getAllAvailableActions();
                    // Choose Action gives the highest immediate reward (score)
                    if (rollout_strategy){
                        GameState best_state = simulation_state.copy();
                        best_state.advance(allAvailableActions.get(0), true);
                        fmCallsCount += 1;
                        for (int j=1; j<allAvailableActions.size(); j++){
                            GameState simulation__strategy_state = simulation_state.copy();
                            simulation__strategy_state.advance(allAvailableActions.get(j), true);
                            fmCallsCount += 1;
                            if (simulation__strategy_state.getActiveTribeID() == playerID){
                                if (simulation__strategy_state.getScore(playerID) > best_state.getScore(playerID)){
                                    best_state = simulation__strategy_state;
                                }
                            }else{
                                if (simulation__strategy_state.getScore(playerID) < best_state.getScore(playerID)){
                                    best_state = simulation__strategy_state;
                                }
                            }
                        }
                        simulation_state = best_state;

                    }else {
                        simulation_state.advance(allAvailableActions.get(m_rnd.nextInt(allAvailableActions.size())), true);
                        fmCallsCount += 1;
                    }
                } else {
                    break;
                }
            }

            return normalise(rootStateHeuristic.evaluateState(rootState, simulation_state), 0, 1);
        }
        return normalise(rootStateHeuristic.evaluateState(rootState, currentNode.getGameState()), 0, 1);
    }

    private void backUp(TreeNode node, double result)
    {
        TreeNode n = node;
        while(n != null)
        {
            n.setnVisits(n.getnVisits() + 1);
            n.setTotValue(n.getTotValue() + result);

            if (result < bounds[0]) {
                bounds[0] = result;
            }
            if (result > bounds[1]) {
                bounds[1] = result;
            }

            n = n.getParent();
        }
    }

    private double normalise(double a_value, double a_min, double a_max)
    {
        if(a_min < a_max)
            return (a_value - a_min)/(a_max - a_min);
        else    // if bounds are invalid, then return same value
            return a_value;
    }

    public void report(TreeNode node){
        System.out.println("--------------------------------------");
        System.out.println("Turn: " + rootState.getTick());
        System.out.println("Iteration: " + numIters);
        System.out.println("Root Action Number: " + root.totalAction());
        System.out.println("Root Node expandable: " + root.isExpandable());
        System.out.println("Root Node explored actions: " + root.getExploredNodes().size());
        System.out.println("Root Node unexplored actions: " + root.getUnexploredNodes().size());
        System.out.println("Depth of Tree: " + depth);
        System.out.println("Total Nodes of Tree: " + numNodes);

        System.out.println("Best Action: " + node.getAction());
        System.out.println("Best Action Node expandable: " + node.isExpandable());
        System.out.println("Best Action Node visited time: " + node.getnVisits());
        System.out.println("Best Action Node explored actions: " + node.getExploredNodes().size());
        System.out.println("Best Action Node unexplored actions: " + node.getUnexploredNodes().size());
        System.out.println("Best Action Value (Normalised): " + node.normaliseValue(bounds));
        System.out.println("Best Action UCT Value (Without Noise): " + node.uctValueWithoutNoise(bounds));
    }


    @Override
    public String toString() {
        System.out.println("--------------------------------------------------");
        System.out.println(traversePreOrder());
        return "";
    }

    // Print the Tree Structure
    public String traversePreOrder() {

        if(root == null){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.toString());

        String pointEnd = "└──";
        String point = "├──";

        int count = 0;
        Collections.sort(root.getExploredNodes());
        for (TreeNode child: root.getExploredNodes()) {
            if (root.getUnexploredNodes().size() + root.getInvalidNodes().size() > 0){
                traverseNodes(sb, "", point, child, true);
            }else{
                if (count == root.getExploredNodes().size() - 1){
                    traverseNodes(sb, "", pointEnd, child, false);
                }else{
                    traverseNodes(sb, "", point, child, true);
                }
                count++;
            }

        }

        count = 0;
        for (TreeNode child: root.getUnexploredNodes()) {
            if (root.getInvalidNodes().size() > 0){
                traverseNodes(sb, "", point, child, true);
            }else{
                if (count == root.getUnexploredNodes().size() - 1){
                    traverseNodes(sb, "", pointEnd, child, false);
                }else{
                    traverseNodes(sb, "", point, child, true);
                }
                count++;
            }
        }
        for (TreeNode child: root.getInvalidNodes()) {
            if (count == root.getInvalidNodes().size() - 1){
                traverseNodes(sb, "", pointEnd, child, false);
            }else{
                traverseNodes(sb, "", point, child, true);
            }
            count++;
        }

        return sb.toString();
    }

    public void traverseNodes(StringBuilder sb, String padding, String pointer, TreeNode node, boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.toString());

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointEnd = "└──";
            String point = "├──";

            Collections.sort(node.getExploredNodes());
            int count = 0;
            for (TreeNode child: node.getExploredNodes()) {
                if (node.getUnexploredNodes().size() + node.getInvalidNodes().size() > 0){
                    traverseNodes(sb, paddingForBoth, point, child, true);
                }else{
                    if (count == node.getExploredNodes().size() - 1){
                        traverseNodes(sb, paddingForBoth, pointEnd, child, false);
                    }else{
                        traverseNodes(sb, paddingForBoth, point, child, true);
                    }
                    count++;
                }

            }

            count = 0;
            for (TreeNode child: node.getUnexploredNodes()) {
                if (node.getInvalidNodes().size() > 0){
                    traverseNodes(sb, paddingForBoth, point, child, true);
                }else{
                    if (count == node.getUnexploredNodes().size() - 1){
                        traverseNodes(sb, paddingForBoth, pointEnd, child, false);
                    }else{
                        traverseNodes(sb, paddingForBoth, point, child, true);
                    }
                    count++;
                }
            }
            for (TreeNode child: node.getInvalidNodes()) {
                if (count == node.getInvalidNodes().size() - 1){
                    traverseNodes(sb, paddingForBoth, pointEnd, child, false);
                }else{
                    traverseNodes(sb, paddingForBoth, point, child, true);
                }
                count++;
            }
        }
    }

}
