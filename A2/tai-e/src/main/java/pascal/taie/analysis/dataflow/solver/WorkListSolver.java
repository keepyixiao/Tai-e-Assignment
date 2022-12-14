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
import pascal.taie.analysis.dataflow.analysis.constprop.CPFact;
import pascal.taie.analysis.dataflow.fact.DataflowResult;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.ir.stmt.Stmt;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class WorkListSolver<Node, Fact> extends Solver<Node, Fact> {

    WorkListSolver(DataflowAnalysis<Node, Fact> analysis) {
        super(analysis);
    }

    @Override
    protected void doSolveForward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        // TODO - finish me
        LinkedList<Stmt> workList = new LinkedList<>();
        HashSet<Stmt> visited = new HashSet<>();
        for (Node node : cfg) {
            if (!cfg.isEntry(node)) {
                workList.add((Stmt) node);
                visited.add((Stmt) node);
            }
        }

        while (workList.size() > 0) {
            Stmt firstStmt = workList.poll();
            visited.remove(firstStmt);
            Set<Node> precursors = cfg.getPredsOf((Node) firstStmt);
            CPFact inFact = null;
            for (Node precursor : precursors) {
                CPFact preOutFact = (CPFact) result.getOutFact(precursor);
                if (inFact == null) {
                    inFact = preOutFact;
                } else {
                    analysis.meetInto((Fact) inFact, (Fact) preOutFact);
                }
            }

            CPFact oldNodeOutFact = (CPFact) result.getOutFact((Node) firstStmt);
            boolean isChanged = analysis.transferNode((Node) firstStmt, (Fact) inFact, (Fact) oldNodeOutFact);
            if (isChanged) {
                Set<Node> succeeds = cfg.getSuccsOf((Node) firstStmt);
                for (Node succeed : succeeds) {
                    if (!visited.contains(succeed)) {
                        workList.add((Stmt) succeed);
                        visited.add((Stmt) succeed);
                    }
                }
            }

        }
    }

    @Override
    protected void doSolveBackward(CFG<Node> cfg, DataflowResult<Node, Fact> result) {
        throw new UnsupportedOperationException();
    }
}
