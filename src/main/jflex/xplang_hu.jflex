package ppke.itk.xplang.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ppke.itk.xplang.common.Location;
import ppke.itk.xplang.common.CursorPosition;

import java.io.IOException;

/**
 * Lexer for the Hungarian PLanG variant.
 */

%%

%class PlangHuLexer
%implements Lexer
%function next
%type Token

%caseless
%unicode
%line
%column

%{
    private final static Logger log = LoggerFactory.getLogger("Root.Lexer");
%}

%{
    private Token token(Symbol symbol, String lexeme, int line, int column) {
        CursorPosition start = new CursorPosition(line + 1, column + 1);
        CursorPosition end = new CursorPosition(line + 1, column + lexeme.length() + 1);
        log.debug("{}: {}", symbol.name(), yytext());
        return new Token(symbol, lexeme, new Location(start, end));
    }

    @Override
    public void skipToNextLine() throws IOException {
        Token act = null;
        do {
            act = next();
        } while (act.symbol() != Symbol.EOL && act.symbol() != Symbol.EOF && act.symbol() != Symbol.LEXER_ERROR);
    }
%}

A = [aA\u00E1\u00C1]
E = [eE\u00E9\u00C9]
I = [Ii\u00CD\u00ED]
O = [Oo\u00D3\u00F3]
OE = [Oo\u00D6\u0150\u00D5\u00D4\u00F6\u0151\u00F5\u00F4]
U = [Uu\u00DA\u00FA]
UE = [Uu\u00DC\u0170\u0168\u00DB\u00FC\u0171\u0169\u00FB]

Identifier = [a-zA-Z\u00E1\u00C1\u00E9\u00C9\u00CD\u00ED\u00D3\u00F3\u00D6\u0150\u00D5\u00D4\u00F6\u0151\u00F5\u00F4\u00DA\u00FA\u00DC\u0170\u0168\u00DB\u00FC\u0171\u0169\u00FB_?][a-zA-Z0-9\u00E1\u00C1\u00E9\u00C9\u00CD\u00ED\u00D3\u00F3\u00D6\u0150\u00D5\u00D4\u00F6\u0151\u00F5\u00F4\u00DA\u00FA\u00DC\u0170\u0168\u00DB\u00FC\u0171\u0169\u00FB_?.]*

%%

<YYINITIAL> {
    "Program"                       { return token(Symbol.PROGRAM, yytext(), yyline, yycolumn); }
    Program_v{E}ge                  { return token(Symbol.END_PROGRAM, yytext(), yyline, yycolumn); }
    f{UE}ggv{E}ny                   { return token(Symbol.FUNCTION, yytext(), yyline, yycolumn); }
    f{UE}ggv{E}ny_v{E}ge            { return token(Symbol.END_FUNCTION, yytext(), yyline, yycolumn); }
    "majd_lesz"                     { return token(Symbol.FORWARD_DECLARATION, yytext(), yyline, yycolumn); }
    V{A}ltoz{O}k                    { return token(Symbol.DECLARE, yytext(), yyline, yycolumn); }
    "ha"                            { return token(Symbol.IF, yytext(), yyline, yycolumn); }
    "akkor"                         { return token(Symbol.THEN, yytext(), yyline, yycolumn); }
    "vagy_ha"                       { return token(Symbol.ELSIF, yytext(), yyline, yycolumn); }
    k{UE}l{OE}nben                  { return token(Symbol.ELSE, yytext(), yyline, yycolumn); }
    ha_v{E}ge                       { return token(Symbol.ENDIF, yytext(), yyline, yycolumn); }
    "ciklus"                        { return token(Symbol.LOOP, yytext(), yyline, yycolumn); }
    am{I}g                          { return token(Symbol.WHILE, yytext(), yyline, yycolumn); }
    ciklus_v{E}ge                   { return token(Symbol.END_LOOP, yytext(), yyline, yycolumn); }
    "szerintem"                     { return token(Symbol.ASSERT, yytext(), yyline, yycolumn); }
    "be"                            { return token(Symbol.IN, yytext(), yyline, yycolumn); }
    "ki"                            { return token(Symbol.OUT, yytext(), yyline, yycolumn); }
    "megnyit"                       { return token(Symbol.OPEN, yytext(), yyline, yycolumn); }
    lez{A}r                         { return token(Symbol.CLOSE, yytext(), yyline, yycolumn); }

    ":="                            { return token(Symbol.ASSIGNMENT, yytext(), yyline, yycolumn); }
    ":"                             { return token(Symbol.COLON, yytext(), yyline, yycolumn); }
    ","                             { return token(Symbol.COMMA, yytext(), yyline, yycolumn); }
    "("                             { return token(Symbol.PAREN_OPEN, yytext(), yyline, yycolumn); }
    ")"                             { return token(Symbol.PAREN_CLOSE, yytext(), yyline, yycolumn); }
    "["                             { return token(Symbol.BRACKET_OPEN, yytext(), yyline, yycolumn); }
    "]"                             { return token(Symbol.BRACKET_CLOSE, yytext(), yyline, yycolumn); }

    "nem"                           { return token(Symbol.OPERATOR_NOT, yytext(), yyline, yycolumn); }
    "vagy"                          { return token(Symbol.OPERATOR_OR, yytext(), yyline, yycolumn); }
    {E}s                            { return token(Symbol.OPERATOR_AND, yytext(), yyline, yycolumn); }
    "-"                             { return token(Symbol.OPERATOR_MINUS, yytext(), yyline, yycolumn); }
    "+"                             { return token(Symbol.OPERATOR_PLUS, yytext(), yyline, yycolumn); }
    "*"                             { return token(Symbol.OPERATOR_TIMES, yytext(), yyline, yycolumn); }
    "/"                             { return token(Symbol.OPERATOR_DIV, yytext(), yyline, yycolumn); }
    "div"                           { return token(Symbol.OPERATOR_IDIV, yytext(), yyline, yycolumn); }
    "mod"                           { return token(Symbol.OPERATOR_IMOD, yytext(), yyline, yycolumn); }
    "^"                             { return token(Symbol.OPERATOR_EXP, yytext(), yyline, yycolumn); }
    "|"                             { return token(Symbol.OPERATOR_PIPE, yytext(), yyline, yycolumn); }
    "="                             { return token(Symbol.OPERATOR_EQ, yytext(), yyline, yycolumn); }
    "/="                            { return token(Symbol.OPERATOR_NEQ, yytext(), yyline, yycolumn); }
    "<"                             { return token(Symbol.OPERATOR_LT, yytext(), yyline, yycolumn); }
    "<="                            { return token(Symbol.OPERATOR_LTE, yytext(), yyline, yycolumn); }
    ">"                             { return token(Symbol.OPERATOR_GT, yytext(), yyline, yycolumn); }
    ">="                            { return token(Symbol.OPERATOR_GTE, yytext(), yyline, yycolumn); }
    "@"                             { return token(Symbol.OPERATOR_FIND, yytext(), yyline, yycolumn); }
    "SV"                            { return token(Symbol.OPERATOR_SV, yytext(), yyline, yycolumn); }

    \d+                             { return token(Symbol.LITERAL_INT, yytext(), yyline, yycolumn); }
    \d+\.\d+                        { return token(Symbol.LITERAL_REAL, yytext(), yyline, yycolumn); }
    "igaz"                          { return token(Symbol.LITERAL_TRUE, yytext(), yyline, yycolumn); }
    "hamis"                         { return token(Symbol.LITERAL_FALSE, yytext(), yyline, yycolumn); }
    '.'                             { return token(Symbol.LITERAL_CHAR, yytext(), yyline, yycolumn); }
    \"[^\"]*\"                      { return token(Symbol.LITERAL_STRING, yytext(), yyline, yycolumn); }

    {Identifier}                    { return token(Symbol.IDENTIFIER, yytext(), yyline, yycolumn); }

    \*\*[^\r\n]*                    { return token(Symbol.COMMENT, yytext(), yyline, yycolumn); }
    \r|\n|\r\n                      { return token(Symbol.WHITESPACE, yytext(), yyline, yycolumn); }
    [ \t\f]                         { return token(Symbol.EOL, yytext(), yyline, yycolumn); }

    <<EOF>>                         { return token(Symbol.EOF, yytext(), yyline, yycolumn); }
    [^]                             { return token(Symbol.LEXER_ERROR, yytext(), yyline, yycolumn); }
}
