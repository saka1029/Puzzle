package puzzle.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <pre>
 * [SYNTAX]
 * file        ::= record { newline record } [newline]
 * record      ::= field { ',' field }
 * field       ::= { quoted | unquoted }
 * quoted      ::= '"' { textdata | ',' | newline | '"' '"' | '\' '"' } '"'
 * unquoted    ::= { textdata }
 * newline     ::= '\r' [ '\n' ] | '\n'
 * textdata    ::= any character except '\r', '\n', ',', '"'
 * </pre>
 *
 * RFC4180 Common Format and MIME Type for Comma-Separated Values (CSV) Files
 *
 * <pre>
 * [RFC4180 ABNF]
 * file = [header CRLF] record *(CRLF record) [CRLF]
 * header = name *(COMMA name)
 * record = field *(COMMA field)
 * name = field
 * field = (escaped / non-escaped)
 * escaped = DQUOTE *(TEXTDATA / COMMA / CR / LF / 2DQUOTE) DQUOTE
 * non-escaped = *TEXTDATA
 * COMMA = %x2C
 * CR = %x0D ;as per section 6.1 of RFC 2234 [2]
 * DQUOTE =  %x22 ;as per section 6.1 of RFC 2234 [2]
 * LF = %x0A ;as per section 6.1 of RFC 2234 [2]
 * CRLF = CR LF ;as per section 6.1 of RFC 2234 [2]
 * TEXTDATA =  %x20-21 / %x23-2B / %x2D-7E
 * </pre>
 *
 */
public class CSVReader extends Parser {

    public CSVReader(BufferedReader reader) throws IOException {
        super(reader);
    }

    private List<String> line;
    private StringBuilder field = new StringBuilder();

    void quoted() throws IOException {
        while (true) {
            if (ch == EOF)  // 引用符が開いたままEOFとなるケース（あえてエラーとしない）
                break;
            else if (eat(QUOTE) || eat(BACKSLASH))
                if (eat(QUOTE)) // 引用符自体の指定
                    append(field, QUOTE);
                else
                    break;      // 単独の引用符
            else
                appendGet(field, ch);
        }
    }

    void unquoted() throws IOException {
        while (ch != EOF && ch != COMMA && ch != QUOTE && ch != CR && ch != LF)
            appendGet(field, ch);
    }

    void field() throws IOException {
        field.setLength(0);
        while (true)
            if (eat(QUOTE))
                quoted();
            else  if (ch != EOF && ch != COMMA && ch != CR && ch != LF)
                unquoted();
            else
                break;
        line.add(field.toString());
    }

    void eol() throws IOException {
        boolean eolFound = false;
        // skip CR, CRLF or LF
        if (eat(CR)) eolFound = true;
        if (eat(LF)) eolFound = true;
        if (!eolFound && ch != EOF)
            throw new RuntimeException("CR, LF, CRLF or EOF expected");
    }

    public List<String> readLine() throws IOException {
        if (ch == -1)
            return null;
        line = new ArrayList<>();
        field();
        while (eat(COMMA))
            field();
        eol();
        return line;
    }
}
