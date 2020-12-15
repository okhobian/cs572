package edu.iastate.cs572.proj2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This is a utility class
 * Contains static helper methods for other classes
 *
 * @author okhobian
 */
public class Utils
{
    static final String KEY_WORD_KB = "Knowledge Base:";
    static final String KEY_WORD_PROVE = "Prove the following sentences by refutation:";
    static final String LOGIC_NOT = "~";
    static final String LOGIC_AND = "&&";
    static final String LOGIC_OR = "||";
    static final String LOGIC_IF = "=>";
    static final String LOGIC_IFF = "<=>";
    static final String LOGIC_LEFT_BRACKET = "(";
    static final String LOGIC_RIGHT_BRACKET = ")";
    static final String LOGIC_EMPTY = "empty";
    static final String LOGIC_NOT_EMPTY = "not-empty";
    static final String LOGIC_TRUE = "true";
    static final String LOGIC_FALSE = "false";

    /**
     Helper method to read from input file
     @param fName file name in String format ("example.txt")
     @return a concatenated string of the input file
            use "#" as delimiter to separate KEY_WORD_KB and KEY_WORD_PROVE
            use "@" as delimiter to separate Sentences
     @throws FileNotFoundException
     */
    static String readFile(String fName)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            File file = new File(fName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                String data = scanner.nextLine();

                if ( data.equals(KEY_WORD_KB) || data.equals(KEY_WORD_PROVE) )
                    sb.append("#");
                else if(!data.isEmpty())
                    sb.append(data);
                else
                    sb.append("@");
            }
            scanner.close();
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        return sb.toString();
    }

    static String parseFile(File file)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                String data = scanner.nextLine();

                if ( data.equals(KEY_WORD_KB) || data.equals(KEY_WORD_PROVE) )
                    sb.append("#");
                else if(!data.isEmpty())
                    sb.append(data);
                else
                    sb.append("@");
            }
            scanner.close();
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        return sb.toString();
    }

    /**
     Helper method to split a sentence into operators and operands
     @param sentence a sentence in String format. e.g. ( Rain && Outside ) => Wet
     @return a string array of elements. e.g. {"(","Rain","&&","Outside",")","=>","Wet"}
     */
    static String[] getElements(String sentence)
    {
        ArrayList<String> result = new ArrayList<>();

        int i=0;
        while ( i<sentence.length() )
        {
            char c = sentence.charAt(i);

            if(Character.isUpperCase(c))
            {
                int j;
                for(j=i+1; j<sentence.length(); j++)
                {
                    char curC = sentence.charAt(j);
                    if (curC == ' ' || isPartOfOperator(curC))
                        break;
                }
                result.add(sentence.substring(i,j));
                i=j-1;
            }

            switch (c)
            {
                case ('~'):
                    result.add(LOGIC_NOT);
                    break;
                case ('('):
                    result.add(LOGIC_LEFT_BRACKET);
                    break;
                case (')'):
                    result.add(LOGIC_RIGHT_BRACKET);
                    break;
                case ('&'):
                    result.add(LOGIC_AND);
                    i++;
                    break;
                case ('|'):
                    result.add(LOGIC_OR);
                    i++;
                    break;
                case ('='):
                    result.add(LOGIC_IF);
                    i++;
                    break;
                case ('<'):
                    result.add(LOGIC_IFF);
                    i+=2;
                    break;
            }
            i++;
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     Helper method to determine if a character is part of an operator
     @param c   character
     @return true if it's part of a operator; false otherwise
     */
    static boolean isPartOfOperator(char c)
    {
        return c=='~' || c=='&' || c=='|' || c=='=' || c=='<' || c=='>' || c=='(' || c==')';
    }

    /**
     Helper method to determine if a string is an operator
     @param e   string
     @return true if it's one of the operators; false otherwise
     */
    static boolean isOperator(String e)
    {
        return e.equals(LOGIC_AND)
                || e.equals(LOGIC_OR)
                || e.equals(LOGIC_IF)
                || e.equals(LOGIC_IFF)
                || e.equals(LOGIC_LEFT_BRACKET)
                || e.equals(LOGIC_RIGHT_BRACKET)
                || e.equals(LOGIC_NOT);
    }

    /**
     Defines the precedence of each operator when constructing the expression tree
     @param e   operator
     @return integer precedence of the given operator
     */
    static int precedence(String e)
    {
        switch (e)
        {
            case LOGIC_NOT:
                return 5;
            case LOGIC_AND:
                return 4;
            case LOGIC_OR:
                return 3;
            case LOGIC_IF:
                return 2;
            case LOGIC_IFF:
                return 1;
            case LOGIC_LEFT_BRACKET:
                return -1;
            case LOGIC_RIGHT_BRACKET:
                return 0;
        }
        return 0;
    }

    /**
     Helper method to convert a string array of elements of a sentence
     from infix order to postfix order
     @param inFix   sentence elements in infix order
     @return a string array of elements of the sentence in postfix order
     */
    static String[] infixToPostfix(String[] inFix)
    {
        ArrayList<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (int i=0; i<inFix.length; i++)
        {
            String e = inFix[i];

            // If the scanned character is an operand, add it to result
            if ( !isOperator(e) )
                result.add(e);

            // If the scanned character is an '(', push it to the stack
            else if ( e.equals(LOGIC_LEFT_BRACKET) )
                stack.push(e);

            //  If the scanned character is an ')', pop and add to result from the stack
            //  do it until an '(' is encountered.
            else if ( e.equals(LOGIC_RIGHT_BRACKET) )
            {
                while ( !stack.isEmpty() && !stack.peek().equals(LOGIC_LEFT_BRACKET) )
                    result.add(stack.pop());
                stack.pop();
            }
            else // an operator is encountered
            {
                while ( !stack.isEmpty() && precedence(e) <= precedence(stack.peek()) )
                    result.add(stack.pop());
                stack.push(e);
            }
        }

        // pop rest of the operators from the stack and add to result
        while ( !stack.isEmpty() )
            result.add(stack.pop());

        return result.toArray(new String[result.size()]);
    }

    /**
     Helper method to construct expression tree from postfix elements
     @param postFix a string array of elements of a sentence in postfix order
     @return the root of the expression tree
     */
    static TreeNode constructExpressionTree(String[] postFix)
    {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode t, t1, t2;

        // traverse through every character of input expression
        for(int i=0; i<postFix.length; i++)
        {
            if ( !isOperator(postFix[i]) )  // If operand, push into stack
            {
                t = new TreeNode(postFix[i]);
                stack.push(t);
            }
            else if ( postFix[i].equals(LOGIC_NOT) )  // special case unary '~'. only add left child
            {
                t = new TreeNode(postFix[i]);
                t1 = stack.pop();
                t.left = t1;
                stack.push(t);
            }
            else // other operators
            {
                t = new TreeNode(postFix[i]);

                // pop two top nodes
                t1 = stack.pop();
                t2 = stack.pop();

                // make them children
                t.right = t1;
                t.left = t2;

                // add this subexpression to stack
                stack.push(t);
            }
        }

        // only element will be root of expression tree
        t = stack.peek();
        stack.pop();

        return t;
    }

    /**
     Helper method to recursively traverse the expression tree in post-order
     to get CNF form of a sentence
     @param node the root of the expression tree of the sentence
     @return the sentence in ConjunctiveNormalForm
     */
    static ConjunctiveNormalForm buildCnf(TreeNode node)
    {
        if (node != null)
        {
            if(!isOperator(node.value)) // leaf nodes (operands)
            {
                node.cnf = node.value;  // update node.cnf to the operand
                return new ConjunctiveNormalForm(node.value); // CNF with only one operand
            }
        }

        if( !node.value.equals(LOGIC_NOT) ) // any operator except '~'
        {
            // first recur on left subtree
            ConjunctiveNormalForm left = buildCnf(node.left);

            // then recur on right subtree
            ConjunctiveNormalForm right = buildCnf(node.right);

            // evaluate
            ConjunctiveNormalForm eva =
                    new ConjunctiveNormalForm(left.getCNF(), right.getCNF(), node.value);

            // assign cnf to node
            node.cnf = eva.getCNF();
            return eva;
        }
        else // for '~', only one child
        {
            ConjunctiveNormalForm left = buildCnf(node.left);
            ConjunctiveNormalForm eva =
                    new ConjunctiveNormalForm(left.getCNF(), "", node.value);

            node.cnf = eva.getCNF();
            return eva;
        }
    }

    /**
     Helper method to print the expression tree in in-order
     @param node the root of the expression tree of the sentence
     */
    static void inoOrder(TreeNode node)
    {
        if (node != null)
        {
            inoOrder(node.left);
            System.out.print(node.value + " ");
            inoOrder(node.right);
        }
    }

    /**
     Helper method to print the expression tree in post-order (CNF at each node)
     @param node the root of the expression tree of the sentence
     */
    static void postorder(TreeNode node)
    {
        if (node == null)
            return;

        postorder(node.left);
        postorder(node.right);

//        System.out.print(node.value + " ");
        System.out.println(node.cnf );
    }

    /**
     Helper method to check if two clauses contains the same set of literals
     @param literals1   first clause in array of literals
     @param literals2   second clause in array of literals
     @return true if they have the same set of literals; false otherwise
     */
    static boolean containsSameLiterals(String[] literals1, String[] literals2)
    {
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();

        for(String literal : literals1)
            if (literal.charAt(0) == '~')
                list1.add( literal.substring(1) );
            else
                list1.add( literal );

        for(String literal : literals2)
            if (literal.charAt(0) == '~')
                list2.add( literal.substring(1) );
            else
                list2.add( literal );

        return list1.containsAll(list2) || list2.containsAll(list1);
    }

    /**
     Helper method to get a list of opposite literals from two clauses
     NOTE:
     1) This method is based on the assumption that containsSameLiterals is true
     2) Assume clause1 has more literals than clause2 (or equal)
     @param literals1   first clause in array of literals
     @param literals2   second clause in array of literals
     @return a list of opposite literals
     */
    static List<String> getOpposites(String[] literals1, String[] literals2)
    {
        List<String> opposites = new ArrayList<>();
        for(int i=0; i<literals1.length; i++)
        {
            for(int j=0; j<literals2.length; j++)
            {
                if( isOpposite(literals1[i], literals2[j]) )
                    opposites.add(literals1[i]);
            }
        }
        return opposites;
    }

    /**
     Helper method to determine if two literals are opposite to each other
     @param literal1   first literal
     @param literal2   second literal
     @return true if they are opposite (~Q and Q); false otherwise
     */
    static boolean isOpposite(String literal1, String literal2)
    {
        String removeNot1 = literal1.replaceAll(Utils.LOGIC_NOT, "");
        String removeNot2 = literal2.replaceAll(Utils.LOGIC_NOT, "");

        if ( removeNot1.equals(removeNot2) && literal1.length() != literal2.length())
            return true;

        return false;
    }

    /**
     Helper method to remove the opposite literals from a clause
     @param literals   clause in array of literals
     @param opposites   a list of opposite literals
     @return a String array of opposite literals removed from clause
     */
    static String[] removeOpposites(String[] literals, List<String> opposites)
    {
        List<String> list = new ArrayList<>(Arrays.asList(literals));

        for(String opposite : opposites)
            list.remove(opposite);

        return list.toArray(new String[0]);
    }

    /**
     Helper method to union two lists (no duplicate items)
     @param list1   first list
     @param list2   second list
     @return a merged list with no duplicate items
     */
    static ArrayList<String> union(ArrayList<String> list1, ArrayList<String> list2)
    {
        HashSet<String> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

    /**
     Helper method to print each step during resolution
     @param c1  clause1
     @param c2  clause2
     @param resolvent   resolvent of the two clauses
     */
    static void printResolveStep(String c1, String c2, String resolvent)
    {
        System.out.println(c1);
        System.out.println(c2);
        System.out.println("--------------------");
        System.out.println(resolvent+"\n");
    }

    /**
     Helper method to print a 2D string array
     @param mat input matrix
     */
    static void print2D(String mat[][])
    {
        for (String[] row : mat)
            System.out.println(Arrays.toString(row));
    }

}