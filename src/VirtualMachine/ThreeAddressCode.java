package VirtualMachine;

public class ThreeAddressCode {
    public enum Commands{
        //  = const
        ICAAS, // int
        RCAAS, // double
        BCAAS, // bool

        //  =
        IASS, // int assignment
        RASS, // double assignment
        BASS, // bool assignment

        // +=
        IASSADD, // += int
        RASSADD, // += double

        // -=
        IASSSUB, // -= int
        RASSSUB, // -= double

        // *=
        IASSMUL, // *= int
        RASSMUL, // *= double

        // /=
        IASSDIV, // /= int
        RASSDIV, // /= double

        // a + b
        IADD, // int + int
        RADD, // double + double

        // a - b
        ISUB, // int - int
        RSUB, // double - double

        // a * b
        IMUL, // int * int
        RMUL, // double * double

        // a / b
        IDIV, // int / int
        RDIV, // double / double

        // a < b
        ILT, // int less then
        RLT, // double less then

        // a > b
        IGT, // int greater then
        RGT, // int greater then

        // a >= b
        IGEQ, // int >= int
        RGEQ, // double >= double

        // a <= b
        ILEQ, // int <= int
        RLEQ, // double <= double

        // a == b
        IEQ, // int == int
        REQ, // double == double
        BEQ, // bool == bool

        // a != b
        INEQ, // int != int
        RNEQ, // double != double
        BNEQ, // bool != bool

        CONITR, // convert int to double
        CALL, // func call
        PARAM, // parameter passing

        IIF, // cond jump
        IFN, // if not

        LABEL, // label marker
        GOTO, // uncond jump
        STOP// stop
    }

    public Commands command;
    public int indexInMemory = -1;

    public int IValue;
    public double RValue;
    public boolean BValue;

    public int indexOfFirstOperand = -1;
    public int indexOfSecondOperand = -1;
    public String label;
    public ValueType value;

    // команды без параметров
    public static ThreeAddressCode create(Commands command){
        var code = new ThreeAddressCode();
        code.command = command;
        return code;
    }

    // команды с одним индексом памяти
    public static ThreeAddressCode create(Commands command, int indexInMemory){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        return code;
    }

    // для константных присваиваний
    public static ThreeAddressCode createConst(Commands command, int indexInMemory, int IValue){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.IValue = IValue;
        return code;
    }

    public static ThreeAddressCode createConst(Commands command, int indexInMemory, double RValue){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.RValue = RValue;
        return code;
    }

    // для команд с меткой
    public static ThreeAddressCode create(Commands command, String label){
        var code = new ThreeAddressCode();
        code.command = command;
        code.label = label;
        return code;
    }

    // для условных переходов с индексом и меткой
    public static ThreeAddressCode create(Commands command, int indexInMemory, String label){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = indexInMemory;
        code.label = label;
        return code;
    }

    // присваивания между переменными
    public static ThreeAddressCode createAssign(Commands command, int destIndex, int srcIndex){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = destIndex;
        code.indexInMemory = srcIndex;
        return code;
    }

    public static ThreeAddressCode createConvert(Commands command, int destIndex,  int srcIndex){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = destIndex;
        code.indexInMemory = srcIndex;
        return code;
    }

    public static ThreeAddressCode createBinary(Commands command, int indexOfFirstOperand, int indexOfSecondOperand, int resultIndex){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexOfFirstOperand = indexOfFirstOperand;
        code.indexOfSecondOperand = indexOfSecondOperand;
        code.indexInMemory = resultIndex;
        return code;
    }

    public static ThreeAddressCode createAssignOperation(Commands command, int destIndex, int indexOfFirstOperand, int indexOfSecondOperand){
        var code = new ThreeAddressCode();
        code.command = command;
        code.indexInMemory = destIndex;
        code.indexOfFirstOperand = indexOfFirstOperand;
        code.indexOfSecondOperand = indexOfSecondOperand;
        return code;
    }
}
