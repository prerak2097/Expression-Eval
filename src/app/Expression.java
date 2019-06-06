package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			

    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        // get the vars that are in the expression!!!
        // array or array && vars
        //get the vars and then split the string by \\[
    	int kappaInt = 0;

    	String[] tokensFromSplit = expr.split("[^a-zA-Z\\[]+");
    	int counter = tokensFromSplit.length;
    	int otherCounter = 0;
        while(otherCounter<counter)
    	{
    	    if(tokensFromSplit[otherCounter].length() > 0)
    	    {
    	        if(tokensFromSplit[otherCounter].contains("["))
    	        {
    	            StringBuilder sbuildTok = new StringBuilder("");
    	            int xyz = tokensFromSplit[otherCounter].length();
    	            int zyx = 0;
                    while (zyx <xyz)
    	            {
    	                if(tokensFromSplit[otherCounter].charAt(zyx) == '[')
    	                {
    	                    Array arAmy = new Array(sbuildTok.toString());
    	                    if(arrays.isEmpty() || arrays.indexOf(arAmy) == -1) {
    	                        arrays.add(kappaInt++, arAmy);
    	                        System.out.println("array: " +  arrays.toString());
                            }
    	                    sbuildTok.setLength(0);
    	                }
    	                else
    	                {
    	                    sbuildTok.append(tokensFromSplit[otherCounter].charAt(zyx));
                        }
    	               zyx ++;
                    }
    	            if(0 < sbuildTok.length())
    	            {
    	                Variable myVar = new Variable(sbuildTok.toString());
    	                if(vars.indexOf(myVar) == -1 || vars.isEmpty())
    	                {
    	                    vars.add(myVar);
    	                    System.out.println("maroVAR: " + myVar.toString());
                        }
                    }
                }
    	        else //var wasn't added prior
    	        {
    	            Variable myVar = new Variable(tokensFromSplit[otherCounter]);
    	            if(vars.indexOf(myVar) == -1 || vars.isEmpty())
    	            {
    	                vars.add(myVar);
    	                System.out.println("Variable: " + vars.toString());
                    }
                }
            }
    	   otherCounter ++; // keep while from being endless!
        }
    }

    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    

    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays)
    {
        expr.trim();
        Stack<Character> charStack = new Stack<>();
        Stack<String> StringStack = new Stack<>();

        StringBuffer buffer = new StringBuffer("");
        Stack<Float> FloatStack = new Stack<>();

        float pablo =0;
        int i = 0;
        while (i<expr.length())
        {
            if(expr.charAt(i) == '(')
            {
                charStack.push(expr.charAt(i));
            }
            else if(expr.charAt(i) == ')')
            {
                while(charStack.isEmpty() == false && !FloatStack.isEmpty() && (charStack.peek() != '(' ))
                {
                    Calc(FloatStack, charStack);
                }
                if(charStack.peek() == '(')
                {
                    charStack.pop();
                }
            }
            else if (expr.charAt(i) == '[')
            {
                StringStack.push(buffer.toString());
                buffer.setLength(0);
                charStack.push(expr.charAt(i));
            }
            else if (expr.charAt(i) == ']')
            { // closing Bracket found
                while(charStack.isEmpty() != true && FloatStack.isEmpty() != true && (charStack.peek() != '['))
                {
                    Calc(FloatStack, charStack);
                }
                if(charStack.peek() == '[')
                {
                    charStack.pop();
                }
                float index = FloatStack.pop().floatValue();
                Iterator<Array> rator = arrays.iterator();
                while (rator.hasNext())
                {
                    Array ray = rator.next();
                    if(ray.name.equals(StringStack.peek()))
                    {
                        FloatStack.push((float) ray.values[(int)index]);
                        StringStack.pop();
                        break;
                    }
                }
            }
            else if (expr.charAt(i) == ' ')
            {
                break;
            }
            else if (expr.charAt(i) == '+' || expr.charAt(i) == '-' || expr.charAt(i) == '*' || expr.charAt(i) == '/')
            {
                while(charStack.isEmpty() == false && (charStack.peek() != '(') && (charStack.peek() != '[') && whatComesFirst(expr.charAt(i), charStack.peek()))
                {
                    Calc(FloatStack, charStack);
                }
                charStack.push(expr.charAt(i));
            }
            else
                {
                if ((expr.charAt(i) <= 'z' && expr.charAt(i) >= 'a') || (expr.charAt(i) <= 'Z' && expr.charAt(i) >= 'A'))
                {
                    buffer.append(expr.charAt(i));
                    if (expr.length() > 1+i)
                    {
                        if (expr.charAt(i+1) == '-' || expr.charAt(1+i) == '*' || expr.charAt(1+i) == '/' || expr.charAt(1+i) == ']' || expr.charAt(i+1) == ')' || expr.charAt(i+1) == '+')
                        {
                            Variable v = new Variable(buffer.toString());
                            int Index = vars.indexOf(v);
                            pablo = vars.get(Index).value;
                            FloatStack.push(pablo);
                            buffer.setLength(0);
                        }
                    }
                    else
                        {
                        Variable v = new Variable(buffer.toString());
                        float index = vars.indexOf(v);
                        pablo = vars.get((int)index).value;
                        FloatStack.push(pablo);
                        buffer.setLength(0);
                    }
                }
                else if ('0' <= expr.charAt(i) && expr.charAt(i) <= '9')
                {
                    buffer.append(expr.charAt(i));
                    if( expr.length() > i+1)
                    {
                        if(expr.charAt(i+1) == '-' || expr.charAt(1+i) == '*' || expr.charAt(1+i) == '/' || expr.charAt(1+i) == ']' || expr.charAt(i+1) == ')' || expr.charAt(i+1) == '+')
                        {
                            pablo = Float.parseFloat(buffer.toString());
                            FloatStack.push(pablo);
                            System.out.println("daCons: " + pablo);
                            buffer.setLength(0);
                        }
                    }
                    else
                        {
                        pablo = Float.parseFloat(buffer.toString());
                        FloatStack.push(pablo);
                        buffer.setLength(0);
                    }
                }
            }
            i++;
        }
        Float result = Float.valueOf(0);
        if(i==expr.length())
        {
            while ((charStack.size() > 0 && FloatStack.size() >1))
            {
                Calc(FloatStack,charStack);
            }
            if (FloatStack.size() > 0)
            {
                result = FloatStack.pop();
                System.out.println("daRE: " +result.toString());
            }
        }
    	return result.floatValue();
    }
    private static boolean whatComesFirst(char one, char two)
    {
        if (two == ']' || two == '[') //wont be needed but still
        {
            return false;
        }
        else if ((two == ')' || two == ')') && (one != ']' || one != '[')) // wont be needed but still...
        {
            return false;
        }

        if ((one == '*' || one == '/') && (two == '+'|| two == '-')) /// only one necessary is good
        {
            return false;
        }
        return true;
    }
    private static void Calc(Stack <Float> FloatStack, Stack<Character> characterStack)
    {
        Float result = Float.valueOf(0);
        if(FloatStack.size() >1 && characterStack.size() > 0)
        {

            Float var1 = FloatStack.pop().floatValue();
            Float var2 = FloatStack.pop().floatValue();
            switch (characterStack.pop()) {
                case '+' : result  = var2 + var1; break;
                case '-':  result = var2-var1; break;
                case '*':  result = var2*var1; break;
                case '/':  result = var2/var1; break;
            }
            /*if (characterStack.pop() == '/') {
                result = var2 / var1;
            }
            if (characterStack.pop() == '*') {
                result = var2 * var1;
            }
            else if(characterStack.pop() == '+') {
                result = var2 + var1;
            }
            else if (characterStack.pop() == '-') {
                result = var2 - var1;
            } /// aint gonna work cause if else aint optimal in this case
            */
            //push in the RESULT!!!!!!
            FloatStack.push(result);
        }

    }


}
