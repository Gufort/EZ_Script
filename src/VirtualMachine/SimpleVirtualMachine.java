package VirtualMachine;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class SimpleVirtualMachine {
    public static ArrayList<ValueType> memory = new ArrayList<ValueType>();

    private static Hashtable<String, Integer> labelAddresses = new Hashtable<String, Integer>();
    private static Stack<Integer> calls = new Stack<Integer>();
    private static Stack<ValueType> params = new Stack<ValueType>();
    private static ThreeAddressCode[] program;
    private static Hashtable<String, Runnable> standartFunctions = new Hashtable<String, Runnable>();
    static {
        standartFunctions.put("print", () -> executePrintFunction());
    }
    private static int programCounter = 0;

    public static void loadProgram(ArrayList<ThreeAddressCode> pr){
        labelAddresses.clear();
        for(int i = 0; i < pr.size(); i++){
            if(pr.get(i).command == ThreeAddressCode.Commands.LABEL
            && pr.get(i).label != null)
                labelAddresses.put(pr.get(i).label, i);
        }
        program = pr.toArray(new ThreeAddressCode[pr.size()]);
        programCounter = 0;
        // проходим по всей программе и ищем метки
    }

    public static void run() throws Exception{
        if(program == null) throw new Exception("Program isn't loaded!");
        programCounter = 0;

        while(programCounter < program.length){
            var command = program[programCounter];
            execute(command);
            if(command.command == ThreeAddressCode.Commands.STOP)
                break;
            programCounter++;
        }
    }

    public static void resetVM(){
        programCounter = 0;
        calls.clear();
        params.clear();
        labelAddresses.clear();
        program = null;
        memory.clear();
    }

    public static void execute(ThreeAddressCode tar) throws Exception{
        switch(tar.command){
            case ThreeAddressCode.Commands.ICAAS:
                if(tar.IValue != 0)
                    memory.get(tar.indexInMemory).integer = tar.IValue;
                else memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer;
                break;
            case ThreeAddressCode.Commands.RCAAS:
                if(tar.RValue != 0)
                    memory.get(tar.indexInMemory).real = tar.RValue;
                else memory.get(tar.indexInMemory).real = memory.get(tar.indexOfFirstOperand).real;
                break;
            case ThreeAddressCode.Commands.BCAAS: memory.get(tar.indexInMemory).bool = tar.BValue; break;

            case ThreeAddressCode.Commands.IASS:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer;
                break;
            case ThreeAddressCode.Commands.RASS:
                memory.get(tar.indexInMemory).real = memory.get(tar.indexOfFirstOperand).real;
                break;
            case ThreeAddressCode.Commands.BASS:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).bool;
                break;

            case ThreeAddressCode.Commands.IASSADD:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer + memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RASSADD:
                memory.get(tar.indexInMemory).real = memory.get(tar.indexOfFirstOperand).real + memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IASSSUB:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer - memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RASSSUB:
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real - memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IASSMUL:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer * memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RASSMUL:
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real * memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IASSDIV:
                if(memory.get(tar.indexOfSecondOperand).integer == 0)
                    throw new Exception("Divide by zero!!!");
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer / memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RASSDIV:
                if(memory.get(tar.indexOfSecondOperand).real == 0)
                    throw new Exception("Divide by zero!!!");
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real / memory.get(tar.indexOfSecondOperand).real;
                break;

            case ThreeAddressCode.Commands.IADD:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer + memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RADD:
                memory.get(tar.indexInMemory).real = memory.get(tar.indexOfFirstOperand).real + memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.ISUB:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer - memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RSUB:
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real - memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IMUL:
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer * memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RMUL:
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real * memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IDIV:
                if(memory.get(tar.indexOfSecondOperand).integer == 0)
                    throw new Exception("Divide by zero!!!");
                memory.get(tar.indexInMemory).integer = memory.get(tar.indexOfFirstOperand).integer / memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RDIV:
                if(memory.get(tar.indexOfSecondOperand).real == 0)
                    throw new Exception("Divide by zero!!!");
                memory.get(tar.indexInMemory).real  = memory.get(tar.indexOfFirstOperand).real / memory.get(tar.indexOfSecondOperand).real;
                break;

            case ThreeAddressCode.Commands.ILT:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer < memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RLT:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real < memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IGT:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer > memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RGT:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real > memory.get(tar.indexOfSecondOperand).real;
                break;

            case ThreeAddressCode.Commands.ILEQ:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer <= memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RLEQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real <= memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.IGEQ:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer >= memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RGEQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real >= memory.get(tar.indexOfSecondOperand).real;
                break;

            case ThreeAddressCode.Commands.IEQ:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer == memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.REQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real == memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.BEQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).bool == memory.get(tar.indexOfSecondOperand).bool;
                break;
            case ThreeAddressCode.Commands.INEQ:
                memory.get(tar.indexInMemory).bool = memory.get(tar.indexOfFirstOperand).integer != memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.RNEQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).real != memory.get(tar.indexOfSecondOperand).real;
                break;
            case ThreeAddressCode.Commands.BNEQ:
                memory.get(tar.indexInMemory).bool  = memory.get(tar.indexOfFirstOperand).bool != memory.get(tar.indexOfSecondOperand).bool;
                break;

            case ThreeAddressCode.Commands.CONITR:
                memory.get(tar.indexOfFirstOperand).real =  memory.get(tar.indexOfSecondOperand).integer;
                break;
            case ThreeAddressCode.Commands.CALL:
                if(standartFunctions.containsKey(tar.label)){
                    standartFunctions.get(tar.label).run();

                    // тут потом реализуй функции

                }
                // пользовательские функции
                else if(labelAddresses.containsKey(tar.label)){
                    calls.push(programCounter);
                    programCounter = labelAddresses.get(tar.label) - 1;
                }
                else throw new RuntimeException("Function " + tar.label + " not found");
                break;
            case ThreeAddressCode.Commands.PARAM:
                params.push(memory.get(tar.indexInMemory));
                break;

            case ThreeAddressCode.Commands.IIF:
                if(memory.get(tar.indexInMemory).bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                }
                else throw new RuntimeException("Label " + tar.label + " not found");
                break;
            case ThreeAddressCode.Commands.IFN:
                if(!memory.get(tar.indexInMemory).bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                }
                else throw new RuntimeException("Label " + tar.label + " not found");
                break;

            case ThreeAddressCode.Commands.LABEL:
                break;
            case ThreeAddressCode.Commands.GOTO:
                var address = labelAddresses.get(tar.label);
                if(address != null)
                    programCounter = address - 1;
                else throw new RuntimeException("Label " + tar.label + " not found");
                break;
            case ThreeAddressCode.Commands.STOP:
                break;

            default: throw new ExecutionControl.NotImplementedException("Command" + tar.command + " not implemented!");
        }
    }

    public void startProgram(ArrayList<ThreeAddressCode> program) throws Exception {
        loadProgram(program);
        run();
    }


    private static void executePrintFunction(){
        if(!params.isEmpty()){
            var value = params.pop();
            if(value.type == ValueType.VarValueType.INTEGER)
                System.out.print(value.integer);
            else if(value.type == ValueType.VarValueType.REAL)
                System.out.print(value.real);
            else if(value.type == ValueType.VarValueType.BOOLEAN)
                System.out.print(value.bool);
        }
    }
}
