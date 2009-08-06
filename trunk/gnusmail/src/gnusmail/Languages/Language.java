/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.Languages;

/**
 * This is an enumeration of the currently supported languages
 * @author jmcarmona
 */
public enum Language {
    SPANISH(0),
    ENGLISH(1);


    int codigo;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }



    private Language(int codigo) {
        this.codigo = codigo;
    }

    public String getLanguageName() {
        if (codigo == 0) {
            return "Spanish";
        } else if (codigo == 0) {
            return "English";
        } else {
            return "Unknown";
        }
    }


}
