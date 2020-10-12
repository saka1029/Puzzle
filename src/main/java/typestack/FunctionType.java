package typestack;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class FunctionType implements Type {

    Logger logger = Logger.getLogger(FunctionType.class.getName());

    final Type[] input;
    final Type[] output;

    FunctionType(Type[] input, Type[] output) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(output, "output");
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        // TODO Auto-generated method stub
        return false;
    }

    void check(Type[] output, Type[] input, int matchLength, FunctionType next) {
        int op = output.length - matchLength;
        int ip = input.length - matchLength;
        for (int k = 0; k < matchLength; ++k, ++op, ++ip) {
            logger.info("check: pos=" + k + " " + output[op] + ":" + input[ip]);
            if (!input[ip].isAssignableFrom(output[op]))
                throw new CompileError("Cannot composite %s and %s (cannot convert %s to %s)",
                    this, next, output[op], input[ip]);
        }
    }

    public FunctionType composite(FunctionType next) {
        int matchLength = Math.min(this.output.length, next.input.length);
        int inputLength = this.input.length + next.input.length - matchLength;
        int outputLength = this.output.length + next.output.length - matchLength;
        check(this.output, next.input, matchLength, next);
        Type[] input = new Type[inputLength];
        int inputRest = next.input.length - matchLength;
        System.arraycopy(next.input, 0, input, 0, inputRest);
        System.arraycopy(this.input, 0, input, inputRest, this.input.length);
        Type[] output = new Type[outputLength];
        int outputRest = this.output.length - matchLength;
        System.arraycopy(this.output, 0, output, 0, outputRest);
        System.arraycopy(next.output, 0, output, outputRest, next.output.length);
        FunctionType result = new FunctionType(input, output);
        return result;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(input) * 31 + Arrays.hashCode(output);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FunctionType other = (FunctionType) obj;
        return Arrays.equals(input, other.input)
            && Arrays.equals(output, other.output);
    }

    @Override
    public String toString() {
        return Arrays.toString(input) + "->" + Arrays.toString(output);
    }
}
