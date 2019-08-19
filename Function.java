import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class Function{
    public String[] params = new String[0];
    public BCParser.FunctionContext ctx;
    public Function(BCParser.FunctionContext ctx){
        this.ctx = ctx;
    }
    public void setParams(String[]inputs){
        this.params = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            this.params[i] = inputs[i];
        }
    }
}

