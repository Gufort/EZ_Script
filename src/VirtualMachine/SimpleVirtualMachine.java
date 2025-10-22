package VirtualMachine;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class SimpleVirtualMachine {
    public static ValueType[] memory = new ValueType[1000];
    private static Consumer<String> outputHandler = System.out::println;

    private static Hashtable<String, Integer> labelAddresses = new Hashtable<String, Integer>();
    private static Stack<Integer> calls = new Stack<Integer>();
    private static Stack<ValueType> params = new Stack<ValueType>();
    private static ThreeAddressCode[] program;
    private static Hashtable<String, Runnable> standartFunctions = new Hashtable<String, Runnable>();
    static {
        standartFunctions.put("Print", () -> executePrintFunction());
    }
    private static int programCounter = 0;

    public static void loadProgram(List<ThreeAddressCode> pr){
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

    public static void initialize()
    {
        for (int i = 0; i < memory.length; i++)
            memory[i] = new ValueType();
    }

    public static void increaseMemorySize(int size) {
        if(size >= memory.length){
            int newSize = Math.max(size + 100, memory.length * 2);
            int oldSize = memory.length;
            ValueType[] newArray = new ValueType[newSize];
            System.arraycopy(memory, 0, newArray, 0, memory.length);
            memory = newArray;
            // Инициализируем новые ячейки
            for(int i = oldSize; i < memory.length; i++) {
                memory[i] = new ValueType();
            }
        }
    }

    public static void resetVM(){
        programCounter = 0;
        calls.clear();
        params.clear();
        labelAddresses.clear();
        program = null;
        initialize();
    }

    public static void execute(ThreeAddressCode tar) throws Exception {
        int maxIndex = Math.max(tar.indexInMemory,
                Math.max(tar.indexOfFirstOperand, tar.indexOfSecondOperand));
        if (maxIndex >= 0) {
            increaseMemorySize(maxIndex + 1);
        }

        switch(tar.command){
            case ThreeAddressCode.Commands.ICAAS:
                if(tar.IValue != 0)
                    memory[tar.indexInMemory].integer = tar.IValue;
                else memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer;
                break;
            case ThreeAddressCode.Commands.RCAAS:
                if(tar.RValue != 0)
                    memory[tar.indexInMemory].real = tar.RValue;
                else memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real;
                break;
            case ThreeAddressCode.Commands.BCAAS:
                memory[tar.indexInMemory].bool = tar.BValue;
                break;

            case ThreeAddressCode.Commands.IASS:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASS:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real;
                break;
            case ThreeAddressCode.Commands.BASS:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool;
                break;

            case ThreeAddressCode.Commands.IASSADD:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer + memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSADD:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real + memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSSUB:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer - memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSSUB:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSMUL:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer * memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSMUL:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real * memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IASSDIV:
                if(memory[tar.indexOfSecondOperand].integer == 0)
                    throw new Exception("Divide by zero!!!");
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer / memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RASSDIV:
                if(memory[tar.indexOfSecondOperand].real == 0)
                    throw new Exception("Divide by zero!!!");
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real / memory[tar.indexOfSecondOperand].real;
                break;

            case ThreeAddressCode.Commands.IADD:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer + memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RADD:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real + memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.ISUB:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer - memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RSUB:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real - memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IMUL:
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer * memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RMUL:
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real * memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IDIV:
                if(memory[tar.indexOfSecondOperand].integer == 0)
                    throw new Exception("Divide by zero!!!");
                memory[tar.indexInMemory].integer = memory[tar.indexOfFirstOperand].integer / memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RDIV:
                if(memory[tar.indexOfSecondOperand].real == 0)
                    throw new Exception("Divide by zero!!!");
                memory[tar.indexInMemory].real = memory[tar.indexOfFirstOperand].real / memory[tar.indexOfSecondOperand].real;
                break;

            case ThreeAddressCode.Commands.ILT:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer < memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RLT:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real < memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IGT:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer > memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RGT:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real > memory[tar.indexOfSecondOperand].real;
                break;

            case ThreeAddressCode.Commands.ILEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer <= memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RLEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real <= memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IGEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer >= memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RGEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real >= memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.IEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer == memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.REQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real == memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.BEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool == memory[tar.indexOfSecondOperand].bool;
                break;
            case ThreeAddressCode.Commands.INEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].integer != memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.RNEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].real != memory[tar.indexOfSecondOperand].real;
                break;
            case ThreeAddressCode.Commands.BNEQ:
                memory[tar.indexInMemory].bool = memory[tar.indexOfFirstOperand].bool != memory[tar.indexOfSecondOperand].bool;
                break;

            case ThreeAddressCode.Commands.CONITR:
                memory[tar.indexOfFirstOperand].real = memory[tar.indexOfSecondOperand].integer;
                break;
            case ThreeAddressCode.Commands.CALL:
                if(standartFunctions.containsKey(tar.label)){
                    standartFunctions.get(tar.label).run();
                }
                // пользовательские функции
                else if(labelAddresses.containsKey(tar.label)){
                    calls.push(programCounter);
                    programCounter = labelAddresses.get(tar.label) - 1;
                }
                else throw new RuntimeException("Function " + tar.label + " not found");
                break;

            case ThreeAddressCode.Commands.PARAM:
                params.push(memory[tar.indexInMemory]);
                break;
            case ThreeAddressCode.Commands.IIF:
                if(memory[tar.indexInMemory].bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                    else throw new RuntimeException("Label " + tar.label + " not found");
                }
                break;
            case ThreeAddressCode.Commands.IFN:
                if(!memory[tar.indexInMemory].bool){
                    var address = labelAddresses.get(tar.label);
                    if(address != null)
                        programCounter = address - 1;
                    else throw new RuntimeException("Label " + tar.label + " not found");
                }
                break;

            case ThreeAddressCode.Commands.PUSH:
                params.push(memory[tar.indexInMemory]);
                break;
            case ThreeAddressCode.Commands.POP:
                if (params.size() > 0)
                    params.pop();


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

            default: throw new RuntimeException("Command " + tar.command + " not implemented!");
        }
    }
    public void startProgram(ArrayList<ThreeAddressCode> program) throws Exception {
        initialize();
        loadProgram(program);
        run();
    }


    private static void executePrintFunction() {
        if (!params.isEmpty()) {
            ValueType value = params.peek();

            if (Math.abs(value.real) > 0.000001) {
                outputHandler.accept(String.format("%.6f", value.real));
            } else {
                outputHandler.accept(Integer.toString(value.integer));
            }
        }
    }
}
