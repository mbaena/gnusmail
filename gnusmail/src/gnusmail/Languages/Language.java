package gnusmail.Languages;

/**
 * This is an enumeration of the currently supported languages
 * @author jmcarmona
 */
public enum Language {
    SPANISH(0),
    ENGLISH(1);

    int code;

    public int getCode() {
        return code;
    }

    public void setCode(int codigo) {
        this.code = codigo;
    }

    private Language(int codigo) {
        this.code = codigo;
    }

    public String getLanguageName() {
        if (code == 0) {
            return "Spanish";
        } else if (code == 0) {
            return "English";
        } else {
            return "Unknown";
        }
    }
}
