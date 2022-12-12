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

package pascal.taie.analysis.dataflow.analysis.constprop;

import pascal.taie.analysis.dataflow.analysis.AbstractDataflowAnalysis;
import pascal.taie.analysis.graph.cfg.CFG;
import pascal.taie.config.AnalysisConfig;
import pascal.taie.ir.IR;
import pascal.taie.ir.exp.*;
import pascal.taie.ir.stmt.DefinitionStmt;
import pascal.taie.ir.stmt.Stmt;
import pascal.taie.language.type.PrimitiveType;
import pascal.taie.language.type.Type;
import pascal.taie.util.AnalysisException;

public class ConstantPropagation extends
        AbstractDataflowAnalysis<Stmt, CPFact> {

    public static final String ID = "constprop";

    public ConstantPropagation(AnalysisConfig config) {
        super(config);
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public CPFact newBoundaryFact(CFG<Stmt> cfg) {
        // TODO - finish me
        return null;
    }

    @Override
    public CPFact newInitialFact() {
        // TODO - finish me
        return null;
    }

    @Override
    public void meetInto(CPFact fact, CPFact target) {
        // TODO - finish me
    }

    /**
     * Meets two Values.
     */
    public Value meetValue(Value v1, Value v2) {
        // TODO - finish me
        if (v1.isConstant()) {
            if (v2.isConstant()) {
                int v1Num = v1.getConstant();
                int v2Num = v2.getConstant();
                if (v1Num == v2Num) {
                    return Value.makeConstant(v1Num);
                } else {
                    return Value.getUndef();
                }
            } else if (v2.isUndef()) {
                ///fixme , is that right?
                return Value.getUndef();
            }

        } else if (v1.isNAC() || v2.isNAC()) {
            return Value.getNAC();
        }
        return Value.getUndef();
    }

    @Override
    public boolean transferNode(Stmt stmt, CPFact in, CPFact out) {
        // TODO - finish me
        return false;
    }

    /**
     * @return true if the given variable can hold integer value, otherwise false.
     */
    public static boolean canHoldInt(Var var) {
        Type type = var.getType();
        if (type instanceof PrimitiveType) {
            switch ((PrimitiveType) type) {
                case BYTE:
                case SHORT:
                case INT:
                case CHAR:
                case BOOLEAN:
                    return true;
            }
        }
        return false;
    }

    /**
     * Evaluates the {@link Value} of given expression.
     *
     * @param exp the expression to be evaluated
     * @param in  IN fact of the statement
     * @return the resulting {@link Value}
     */
    public static Value evaluate(Exp exp, CPFact in) {
        // TODO - finish me
        if (exp instanceof ArithmeticExp) {
            ArithmeticExp arithmeticExp = (ArithmeticExp) exp;
            ArithmeticExp.Op operator = arithmeticExp.getOperator();
            Var operand1 = arithmeticExp.getOperand1();
            Var operand2 = arithmeticExp.getOperand2();
            int result = 0;
            if (operand1.isTempConst() && operand2.isTempConst()) {
                Literal l1 = operand1.getTempConstValue();
                Literal l2 = operand2.getTempConstValue();
                IntLiteral intLiteral1 = (IntLiteral) l1;
                IntLiteral intLiteral2 = (IntLiteral) l2;
                if (operator.equals(ArithmeticExp.Op.ADD)) {
                    result = intLiteral1.getValue() + intLiteral2.getValue();
                } else if (operator.equals(ArithmeticExp.Op.SUB)) {
                    result = intLiteral1.getValue() - intLiteral2.getValue();
                } else if (operator.equals(ArithmeticExp.Op.MUL)) {
                    result = intLiteral1.getValue() * intLiteral1.getValue();
                } else if (operator.equals(ArithmeticExp.Op.DIV)) {
                    result = intLiteral1.getValue() / intLiteral2.getValue();
                } else if (operator.equals(ArithmeticExp.Op.REM)) {
                    result = intLiteral1.getValue() % intLiteral2.getValue();
                }
                return Value.makeConstant(result);
            }
        } else if (exp instanceof ConditionExp) {
            ConditionExp conditionExp = (ConditionExp) exp;
        } else if (exp instanceof BitwiseExp) {
            BitwiseExp bitwiseExp = (BitwiseExp) exp;
            BitwiseExp.Op operator = bitwiseExp.getOperator();
            Var operand1 = bitwiseExp.getOperand1();
            Var operand2 = bitwiseExp.getOperand2();
            int result = 0;
            if (operand1.isTempConst() && operand2.isTempConst()) {
                Literal l1 = operand1.getTempConstValue();
                Literal l2 = operand2.getTempConstValue();
                IntLiteral intLiteral1 = (IntLiteral) l1;
                IntLiteral intLiteral2 = (IntLiteral) l2;

                if (operator.equals(BitwiseExp.Op.AND)) {
                    result = intLiteral1.getValue() & intLiteral2.getValue();
                } else if (operator.equals(BitwiseExp.Op.OR)) {
                    result = intLiteral1.getValue() | intLiteral2.getValue();
                } else if (operator.equals(BitwiseExp.Op.XOR)) {
                    result = intLiteral1.getValue() ^ intLiteral2.getValue();
                }
                return Value.makeConstant(result);
            }
        } else if (exp instanceof ShiftExp) {
            ShiftExp shiftExp = (ShiftExp) exp;
            Var operand1 = shiftExp.getOperand1();
            Var operand2 = shiftExp.getOperand2();
            ShiftExp.Op operator = shiftExp.getOperator();
            int result = 0;

            if (operand1.isTempConst() && operand2.isTempConst()) {
                Literal l1 = operand1.getTempConstValue();
                Literal l2 = operand2.getTempConstValue();
                IntLiteral intLiteral1 = (IntLiteral) l1;
                IntLiteral intLiteral2 = (IntLiteral) l2;

                if (operator.equals(ShiftExp.Op.SHL)) {
                    result = intLiteral1.getValue() << intLiteral1.getValue();
                }else if(operator.equals(ShiftExp.Op.SHR)){
                    result = intLiteral1.getValue() >> intLiteral1.getValue();
                }else if(operator.equals(ShiftExp.Op.USHR)){
                    result = intLiteral1.getValue() >>> intLiteral1.getValue();
                }
                return Value.makeConstant(result);
            }

        }
        return Value.getNAC();
    }
}
