package shell.core;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {
    public static List<String> tokenizer(String input){
        List<String> arguments = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char open = '\0',ch, next_ch;
        int l = input.length();

        for(int i = 0; i < l;i++){
            ch = input.charAt(i);
            //open can be only 3 values, null, ' (single quote), " (double quote)
            if(open == ch){ //quotes
                if(i < l-1 && ch == input.charAt(i+1)){
                    i++;
                } else {
                    open = '\0';
                }
            }
            else if(open == '\0') {
                //checking if no quotes
                //checking whitespace
                if(ch == ' '){
                    addBufToTokens(arguments,sb);
                }
                else if (ch == '\\') { //checking for escape sequence
                    sb.append(input.charAt(i+1));
                    i++;
                }
                else if(ch == '\"' || ch == '\''){
                    //since opening quotes, hence next_ch definitely exists
                    next_ch = input.charAt(i+1);
                    if(ch == next_ch){
                        i++;
                    } else {
                        open = ch; //setting opening quotes
                    }
                }
                else if(ch == '>'){
                    addBufToTokens(arguments,sb);
                    sb.append(ch);
                    addBufToTokens(arguments,sb);
                } else if(ch == '1' && i < l-1 && input.charAt(i+1) == '>'){
                    continue;
                }
                else {
                    sb.append(ch);
                }
            }
            else if(open == '\"'){ //double quotes open
                if(ch == '\\'){
                    next_ch = input.charAt(i+1); //cant be last char, since quotes are open

                    if(next_ch == '\"' || next_ch == '\\'){
                        sb.append(next_ch);
                        i++;
                    } else {
                        sb.append(ch);
                    }
                }
                else {
                    sb.append(ch);
                }
            }
            else {
                //single quotes accept everything literally
                sb.append(ch);
            }
        }
        //if something remains in buffer
        addBufToTokens(arguments,sb);
        return arguments;
    }

    private static void addBufToTokens(List<String> arguments, StringBuilder sb) {
        if (!sb.isEmpty()) {
            arguments.add(sb.toString());
            sb.setLength(0);
        }
    }
}
