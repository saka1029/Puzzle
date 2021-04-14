package puzzle.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
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
public class RFC4180CSVReader extends Parser {

    public RFC4180CSVReader(BufferedReader reader) throws IOException {
        super(reader);
    }

    private List<String> line;
    private StringBuilder field = new StringBuilder();

    void quoted() throws IOException {
        while (true) {
            if (ch == EOF)  // 引用符が開いたままEOFとなるケース
                throw new RuntimeException("unexpected EOF");
            else if (eat(QUOTE))
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
        if (eat(QUOTE))
            quoted();
        else
            unquoted();
        line.add(field.toString());
    }

    void eol() throws IOException {
        if (eat(CR) && eat(LF))
            return;
        else if (ch == EOF)
            return;
        throw new RuntimeException("CRLF or EOF expected");
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
