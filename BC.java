import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.FileInputStream;
import java.io.InputStream;

public class BC{
    public static void main(String[] args) throws Exception{
        String inputFile = null;
        if(args.length > 0) inputFile = args[0];
        InputStream in = System.in;
        if(inputFile != null) in = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream(in);
        BCLexer lexer = new BCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BCParser parser = new BCParser(tokens);
        ParseTree tree = parser.multExpr();
        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);
        
    }
}