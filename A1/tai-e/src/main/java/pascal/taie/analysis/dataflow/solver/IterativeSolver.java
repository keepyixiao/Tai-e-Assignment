/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.dataflow.solver;

import pascal.taie.analysis.dataflow.analysis.DataflowAnalysis;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.dataflow.fact.SetFact;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Stmt;

import java.util.*;

class IterativeSolver<Node, Fact> extends Solver<Node, Fact> {

    public IterativeSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        boolean changeDetected = true;
        Node exitNode = cfg.getExit();
        Set<Node> precursor = cfg.getPredsOf(exitNode);




        while(changeDetected){
            changeDetected = false;
            Set<Node> visited = new HashSet<>();

            while(!precursor.isEmpty()){
                Set<Node> newPrecursor = new HashSet<Node>() ;
                for(Node node : precursor){
                    visited.add(node);
                    SetFact<Var> nodeOutFact = (SetFact<Var>)result.getOutFact(node);
                    nodeOutFact.clear();
                    for(Node nodeNext : cfg.getSuccsOf(node)){
                        SetFact nodeOutNextInFact = (SetFact) result.getInFact(nodeNext);
                        nodeOutFact.union(nodeOutNextInFact);


                    }
                    SetFact<Var> nodeInFact = (SetFact<Var>) result.getInFact(node);


                    boolean isChanged = analysis.transferNode(node, (Fact) nodeInFact, (Fact) nodeOutFact);

                    if(isChanged){
                        changeDetected = true;
                    }

                    for(Node nodePre : cfg.getPredsOf(node)){
                        if(!visited.contains(nodePre)){
                            newPrecursor.add(nodePre);
                        }

                    }


                }
                precursor = newPrecursor;
            }
        }
    }
}
