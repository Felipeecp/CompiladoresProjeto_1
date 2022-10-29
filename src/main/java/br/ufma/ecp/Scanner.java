package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public class Scanner {

    private byte[] input;
    private int current;
    private int start;

    public Scanner (byte[] input) {
        this.input = input;
        current = 0;
        start = 0;
    }

    private void skipLineComentario() {
        char ch = peek();

        if(ch == '/' && peekNext() == '/'){
            for(; ch != '\n' && ch != 0;  advance(), ch = peek());
        }
    }

    private void skipWhitespace() {
        char ch = peek();
        while (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
            advance();
            ch = peek();
        }
    }

    private void skipBlockComments() {
        char ch = peek();
        if(ch == '/' && peekNext() == '*') {
            while(true){
                ch = peek();
                String valueAtualAndNext = String.valueOf(ch) + String.valueOf(peekNext());
                if("*/".equals(valueAtualAndNext)){
                    advance(); // avança o *
                    advance(); // avança a barra
                    break;
                }
                if(ch == 0) throw new Error("syntax error");
                advance();
            }
        }
    }

    public Token nextToken () {

        skipWhitespace();

        start = current;
        char ch = peek();

        if (Character.isDigit(ch)) {
            return number();
        }

        if (Character.isLetter(ch)) {
            return identifier();
        }



        switch (ch) {


            case '"':
                return string();

            case '/':
                if (peekNext() == '/') {
                    skipLineComentario();
                    return nextToken();
                } else if (peekNext() == '*') {
                    skipBlockComments();
                    return nextToken();
                }
                else {
                    advance();
                    return new Token (TokenType.SLASH,"/");
                }

            case '+':
                advance();
                return new Token (TokenType.PLUS,"+");
            case '-':
                advance();
                return new Token (TokenType.MINUS,"-");
            case '*':
                advance();
                return new Token (TokenType.ASTERISK,"*");
            case '.':
                advance();
                return new Token (TokenType.DOT,".");
            case '&':
                advance();
                return new Token (TokenType.AND,"&");
            case '|':
                advance();
                return new Token (TokenType.OR,"|");
            case '~':
                advance();
                return new Token (TokenType.NOT,"~");


            case '>':
                advance();
                return new Token (TokenType.GT,">");
            case '<':
                advance();
                return new Token (TokenType.LT,"<");
            case '=':
                advance();
                return new Token (TokenType.EQ,"=");

            case '(':
                advance();
                return new Token (TokenType.LPAREN,"(");
            case ')':
                advance();
                return new Token (TokenType.RPAREN,")");
            case '{':
                advance();
                return new Token (TokenType.LBRACE,"[");
            case '}':
                advance();
                return new Token (TokenType.RBRACE,"]");
            case '[':
                advance();
                return new Token (TokenType.LBRACKET,"{");
            case ']':
                advance();
                return new Token (TokenType.RBRACKET,"}");
            case ';':
                advance();
                return new Token (TokenType.SEMICOLON,";");
            case ',':
                advance();
                return new Token (TokenType.COMMA,",");

            case 0:
                return new Token(TokenType.EOF, "EOF");
            default:
                advance();
                return new Token(TokenType.ILLEGAL, Character.toString(ch));
        }




    }

    private Token identifier() {
        while (isAlphaNumeric(peek())) advance();

        String id = new String(input, start, current-start, StandardCharsets.UTF_8)  ;
        TokenType type = TokenType.valueOfLexeme(id);
        if (type == null) type = IDENTIFIER;
        return new Token(type, id);
    }

    private Token number() {
        while (Character.isDigit(peek())) {
            advance();
        }
            String num = new String(input, start, current-start, StandardCharsets.UTF_8)  ;
            return new Token(NUMBER, num);
    }

    private Token string () {
        advance();
        start = current;
        while (peek() != '"' && peek() != 0) {
            advance();
        }
        String s = new String(input, start, current-start, StandardCharsets.UTF_8);
        Token token = new Token (TokenType.STRING,s);
        advance();
        return token;
    }

    private void advance()  {
        char ch = peek();
        if (ch != 0) {
            current++;
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
      }
    
      private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || Character.isDigit((c));
      }
    

    private char peek () {
        if (current < input.length)
           return (char)input[current];
       return 0;
    }

    private char peekNext(){
        if (current + 1 < input.length) {
            return (char) input[current+1];
        }
        return 0;
    }


    
}
