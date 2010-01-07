package gnusmail.languagefeatures.stemming;

/**
 *
 * @author jmcarmona
 */
public class RuleEnglish {
  /**
     * Identificador de la regla.
     */
    int id;
    /**
     * Cadena antigua
     */
    String old_end;
    /**
     * Cadena nueva
     */
    String new_end;
    /**
     * Longitud de la antigua cadena
     */
    int old_offset;
    /**
     * Longitud de la nueva cadena
     */
    int new_offset;
    /**
     * Lontitud de la raiz
     */
    int min_root_size;
    /**
     * Condicion que tiene que cumplir una palabra para que pueda aplicarse una regla
     */
    String condition;

    /**
     * Constructor de la clase RuleEnglish
     */
    public RuleEnglish(int idRegla, String oldCad, String newCad, int oldOff, int newOff, int raizMin, String condicion)
    {
        id=idRegla;
        old_end=oldCad;
        new_end=newCad;
        old_offset=oldOff;
        new_offset=newOff;
        min_root_size=raizMin;
        condition=condicion;
    }

    /**
     * Devuelve el identificador de la regla
     * @return Identificacion de la regla.
     */
    public int obtenerId()
    {
        return id;
    }
    /**
     * Devuelve la cadena antigua
     * @return Cadena antigua
     */
    public String obtenerOldEnd()
    {
        return old_end;
    }
/**
     * Devuelve la cadena nueva.
     * @return Cadena nueva.
     */
    public String obtenerNewEnd()
    {
        return new_end;
    }
    /**
     * Devuelve el tamaño de la cadena antigua.
     * @return Tamaño de la cadena antigua.
     */
    public int obtenerOldOffset()
    {
        return old_offset;
    }
    /**
     * Longitud de la nueva terminacion que añadimos.
     * @return Longitud de la nueva terminación.
     */
    public int obtenerNewOffset()
    {
        return new_offset;
    }
    /**
     * Devolvemos el tamaño mínino de la raíz de la palabra.
     * @return Tamaño de la raíz.
     */
    public int obtenerMinRootSize()
    {
        return min_root_size;
    }
    /**
     * Devolvemos la Condicion que tiene que cumplir la palabra para
     * poder aplicar la regla.
     * @return Condicion.
     */
    public String obtenerCondicion()
    {
        return condition;
    }
}
