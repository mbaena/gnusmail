/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gnusmail.languagefeatures.stemming;

import java.util.Enumeration;
import java.util.Vector;

/**
 *
 * @author jmcarmona
 */
public class SpanishStemmer implements IStemmer {
    /**
     * Contiene la posicion del último caracter de la palabra
     */
    int end;
    /**
     * Palabra de la que se quiere extraer su raíz.
     */
    String palabra=new String("");
    /**
     * Raiz de la palabra
     */
    String raizPalabra=new String("");
    /**
     * Lista de reglas de tipo 1.
     */
    Vector listaReglas1=new Vector(1,6);
    /**
     * Lista de reglas de tipo 2.
     */
    Vector listaReglas2=new Vector(1,6);
    /**
     * Lista de reglas de tipo 3.
     */
    Vector listaReglas3=new Vector(1,6);
    /**
     * Lista de reglas de tipo 4.
     */
    Vector listaReglas4=new Vector(1,6);
    /**
     * Lista de reglas de tipo 5.
     */
    Vector listaReglas5=new Vector(1,6);
    /**
     * Lista de reglas de tipo 6.
     */
    Vector listaReglas6=new Vector(1,6);

    /**
     * Constructor de la Clase.
     * Se inicializan las reglas.
     */

    public SpanishStemmer()
    {
        inicializarReglas();
    }

    /**
     * Inicialización de las reglas
     * @return void
     */
    private void inicializarReglas()
    {
         inicializaReglas1();
         inicializaReglas2();
         inicializaReglas3();
         inicializaReglas4();
         inicializaReglas5();
         inicializaReglas6();
    }

    /**
     * Inicializa las reglas de tipo 1
     */
    private void inicializaReglas1()
    {
         RuleSpanish r1=new RuleSpanish(101, "ones", "ón", 3, 1, -1, "NULL");
         RuleSpanish r2=new RuleSpanish(102, "ces", "z", 2, 0, -1, "NULL");
         RuleSpanish r3=new RuleSpanish(103, "es", "", 1, -1, -1, "NULL");
         RuleSpanish r4=new RuleSpanish(104, "s", "", 0, -1, -1, "NULL");
         listaReglas1.addElement(r1);
         listaReglas1.addElement(r2);
         listaReglas1.addElement(r3);
         listaReglas1.addElement(r4);

    }

    /**
     * Inicializa las reglas de tipo 2
     */
    private void inicializaReglas2()
    {

        RuleSpanish r1=new RuleSpanish(201, "aríamos", "ar", 6, 1, -1, "contieneVocal");
        RuleSpanish r2=new RuleSpanish(202, "eríamos", "er", 6, 1, -1, "contieneVocal");
        RuleSpanish r3=new RuleSpanish(203, "iríamos", "ir", 6, 1, -1, "contieneVocal");
        RuleSpanish r4=new RuleSpanish(204, "iésemos", "", 6, -1, -1, "contieneVocal");
        RuleSpanish r5=new RuleSpanish(205, "iéramos", "", 6,-1, -1, "contieneVocal");
        RuleSpanish r6=new RuleSpanish(206, "ábamos", "ar", 6, 1, -1, "contieneVocal");
        RuleSpanish r7=new RuleSpanish(207, "áramos", "ar", 5, 1, -1, "contieneVocal");
        RuleSpanish r8=new RuleSpanish(208, "aremos", "ar", 5, 1, -1, "contieneVocal");
        RuleSpanish r9=new RuleSpanish(209, "ásemos", "er", 5, 1, -1, "contieneVocal");
        RuleSpanish r10=new RuleSpanish(210, "eremos", "ir", 5, 1, -1, "contieneVocal");
        RuleSpanish r11=new RuleSpanish(211, "iremos", "ar", 4, 1, -1, "contieneVocal");
        RuleSpanish r12=new RuleSpanish(212, "uemos", "ar", 4, 1, -1, "contieneVocal");
        RuleSpanish r13=new RuleSpanish(213, "arías", "ar", 4, 1, -1, "contieneVocal");
        RuleSpanish r14=new RuleSpanish(214, "arían", "er", 4, 1, -1, "contieneVocal");
        RuleSpanish r15=new RuleSpanish(215, "erían", "er", 4, 1, -1, "contieneVocal");
        RuleSpanish r16=new RuleSpanish(216, "erías", "ir", 4, 1, -1, "contieneVocal");
        RuleSpanish r17=new RuleSpanish(217, "irían", "ir", 4, 1, -1, "contieneVocal");
        RuleSpanish r18=new RuleSpanish(218, "irías", "", 4,-1, -1, "contieneVocal");
        RuleSpanish r19=new RuleSpanish(219, "iesen", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r20=new RuleSpanish(220, "ieses", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r21=new RuleSpanish(221, "iamos", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r22=new RuleSpanish(222, "ieran", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r23=new RuleSpanish(223, "ieron", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r24=new RuleSpanish(224, "iendo", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r25=new RuleSpanish(225, "ieras", "", 4, -1, -1, "contieneVocal");
        RuleSpanish r26=new RuleSpanish(226, "abas", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r27=new RuleSpanish(227, "aban", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r28=new RuleSpanish(228, "asen", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r29=new RuleSpanish(229, "ases", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r30=new RuleSpanish(230, "ando", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r31=new RuleSpanish(231, "arán", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r32=new RuleSpanish(232, "aran", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r33=new RuleSpanish(233, "arás", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r34=new RuleSpanish(234, "aras", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r35=new RuleSpanish(235, "aron", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r36=new RuleSpanish(236, "aste", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r37=new RuleSpanish(237, "aría", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r38=new RuleSpanish(238, "anza", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r39=new RuleSpanish(239, "arás", "ar", 3, 1, -1, "contieneVocal");
        RuleSpanish r40=new RuleSpanish(240, "erás", "er", 3, 1, -1, "contieneVocal");
        RuleSpanish r41=new RuleSpanish(241, "erán", "er", 3, 1, -1, "contieneVocal");
        RuleSpanish r42=new RuleSpanish(242, "ería", "er", 3, 1, -1, "contieneVocal");
        RuleSpanish r43=new RuleSpanish(243, "emos", "er", 3, 1, -1, "contieneVocal");
        RuleSpanish r44=new RuleSpanish(244, "irán", "ir", 3, 1, -1, "contieneVocal");
        RuleSpanish r45=new RuleSpanish(245, "irás", "ir", 3, 1, -1, "contieneVocal");
        RuleSpanish r46=new RuleSpanish(246, "iera", "", 3,-1, -1, "contieneVocal");
        RuleSpanish r47=new RuleSpanish(247, "iste", "", 3, -1, -1, "contieneVocal");
        RuleSpanish r48=new RuleSpanish(248, "iese", "", 3, -1, -1, "contieneVocal");
        RuleSpanish r49=new RuleSpanish(249, "amos", "", 3,-1, -1, "contieneVocal");
        RuleSpanish r50=new RuleSpanish(250, "imos", "", 3,-1, -1, "contieneVocal");
        RuleSpanish r51=new RuleSpanish(251, "aba", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r52=new RuleSpanish(252, "ado", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r53=new RuleSpanish(253, "ará", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r54=new RuleSpanish(254, "ara", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r55=new RuleSpanish(255, "aré", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r56=new RuleSpanish(256, "ase", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r57=new RuleSpanish(257, "uen", "ar", 2, 1, -1, "contieneVocal");
        RuleSpanish r58=new RuleSpanish(258, "erá", "er", 2, 1, -1, "contieneVocal");
        RuleSpanish r59=new RuleSpanish(259, "eré", "er", 2, 1, -1, "contieneVocal");
        RuleSpanish r60=new RuleSpanish(260, "irá", "ir", 2, 1, -1, "contieneVocal");
        RuleSpanish r61=new RuleSpanish(261, "iré", "ir", 2, 1, -1, "contieneVocal");
        RuleSpanish r62=new RuleSpanish(262, "ían", "", 2,-1, -1, "contieneVocal");
        RuleSpanish r63=new RuleSpanish(263, "ías", "", 2, -1, -1, "contieneVocal");
        RuleSpanish r64=new RuleSpanish(264, "ido", "", 2, -1, -1, "contieneVocal");
        RuleSpanish r65=new RuleSpanish(265, "ué", "ar", 1, 1, -1, "contieneVocal");
        RuleSpanish r66=new RuleSpanish(266, "ía", "", 1,-1, -1, "contieneVocal");
        RuleSpanish r67=new RuleSpanish(267, "ió", "", 1, -1, -1, "contieneVocal");
        RuleSpanish r68=new RuleSpanish(268, "an", "", 1, -1, -1, "contieneVocal");
        RuleSpanish r69=new RuleSpanish(269, "en", "", 1, -1, -1, "contieneVocal");

        listaReglas2.addElement(r1);
        listaReglas2.addElement(r2);
        listaReglas2.addElement(r3);
        listaReglas2.addElement(r4);
        listaReglas2.addElement(r5);
        listaReglas2.addElement(r6);
        listaReglas2.addElement(r7);
        listaReglas2.addElement(r8);
        listaReglas2.addElement(r9);
        listaReglas2.addElement(r10);
        listaReglas2.addElement(r11);
        listaReglas2.addElement(r12);
        listaReglas2.addElement(r13);
        listaReglas2.addElement(r14);
        listaReglas2.addElement(r15);
        listaReglas2.addElement(r16);
        listaReglas2.addElement(r17);
        listaReglas2.addElement(r18);
        listaReglas2.addElement(r19);
        listaReglas2.addElement(r20);
        listaReglas2.addElement(r21);
        listaReglas2.addElement(r22);
        listaReglas2.addElement(r23);
        listaReglas2.addElement(r24);
        listaReglas2.addElement(r25);
        listaReglas2.addElement(r26);
        listaReglas2.addElement(r27);
        listaReglas2.addElement(r28);
        listaReglas2.addElement(r29);
        listaReglas2.addElement(r30);
        listaReglas2.addElement(r31);
        listaReglas2.addElement(r32);
        listaReglas2.addElement(r33);
        listaReglas2.addElement(r34);
        listaReglas2.addElement(r35);
        listaReglas2.addElement(r36);
        listaReglas2.addElement(r37);
        listaReglas2.addElement(r38);
        listaReglas2.addElement(r39);
        listaReglas2.addElement(r40);
        listaReglas2.addElement(r41);
        listaReglas2.addElement(r42);
        listaReglas2.addElement(r43);
        listaReglas2.addElement(r44);
        listaReglas2.addElement(r45);
        listaReglas2.addElement(r46);
        listaReglas2.addElement(r47);
        listaReglas2.addElement(r48);
        listaReglas2.addElement(r49);
        listaReglas2.addElement(r50);
        listaReglas2.addElement(r51);
        listaReglas2.addElement(r52);
        listaReglas2.addElement(r53);
        listaReglas2.addElement(r54);
        listaReglas2.addElement(r55);
        listaReglas2.addElement(r56);
        listaReglas2.addElement(r57);
        listaReglas2.addElement(r58);
        listaReglas2.addElement(r59);
        listaReglas2.addElement(r60);
        listaReglas2.addElement(r61);
        listaReglas2.addElement(r62);
        listaReglas2.addElement(r63);
        listaReglas2.addElement(r64);
        listaReglas2.addElement(r65);
        listaReglas2.addElement(r66);
        listaReglas2.addElement(r67);
        listaReglas2.addElement(r68);
        listaReglas2.addElement(r69);

    }

    /**
     * Inicializa las reglas de tipo 3
     */
    private void inicializaReglas3()
    {
        RuleSpanish r1=new RuleSpanish(301, "ización", "izar", 6, 2, -1, "NULL");
        RuleSpanish r2=new RuleSpanish(302, "cional", "ción", 5, 3, -1, "NULL");
        RuleSpanish r3=new RuleSpanish(303, "ructor", "ruir", 5, 3, -1, "NULL");
        RuleSpanish r4=new RuleSpanish(304, "alismo", "al", 5, 1, -1, "NULL");
        RuleSpanish r5=new RuleSpanish(305, "ductor", "ducir", 5, 4, -1, "NULL");
        RuleSpanish r6=new RuleSpanish(306, "sitor", "ner", 4, 2, -1, "NULL");
        RuleSpanish r7=new RuleSpanish(307, "ante", "ar", 4, 1, -1, "NULL");
        RuleSpanish r8=new RuleSpanish(308, "ador", "ar", 3, 1, -1, "NULL");
        RuleSpanish r9=new RuleSpanish(309, "edor", "er", 3, 1, -1, "NULL");
        listaReglas3.addElement(r1);
        listaReglas3.addElement(r2);
        listaReglas3.addElement(r3);
        listaReglas3.addElement(r4);
        listaReglas3.addElement(r5);
        listaReglas3.addElement(r6);
        listaReglas3.addElement(r7);
        listaReglas3.addElement(r8);
        listaReglas3.addElement(r9);

    }
    /**
     * Inicializa las reglas de tipo 4
     */
    private void inicializaReglas4()
    {
        RuleSpanish r1=new RuleSpanish(401, "ativa", "", 4, -1,  1, "NULL");
        RuleSpanish r2=new RuleSpanish(402, "ativo", "", 4, -1,  1, "NULL");
        listaReglas4.addElement(r1);
        listaReglas4.addElement(r2);

    }

    /**
     * Inicializa las reglas de tipo 5
     */
    private void inicializaReglas5()
    {
        RuleSpanish r1=new RuleSpanish(501, "ilidad", "", 5,-1,  1, "NULL");
        RuleSpanish r2=new RuleSpanish(502, "miento", "", 5,-1,  1, "NULL");
        RuleSpanish r3=new RuleSpanish(503, "mente", "", 4, -1,  1, "NULL");
        RuleSpanish r4=new RuleSpanish(504, "ncial", "n", 4,  0,  0, "NULL");
        RuleSpanish r5=new RuleSpanish(505, "rcial", "rc", 4, 1,  0, "NULL");
        RuleSpanish r6=new RuleSpanish(506, "ancia", "", 4,-1,  1, "NULL");
        RuleSpanish r7=new RuleSpanish(507, "encia", "", 3, -1,  1, "NULL");
        RuleSpanish r8=new RuleSpanish(508, "ista", "", 3, -1,  1, "NULL");
        RuleSpanish r9=new RuleSpanish(509, "ance", "", 3, -1,  1, "NULL");
        RuleSpanish r10=new RuleSpanish(510, "arle", "", 3, -1,  1, "NULL");
        RuleSpanish r11=new RuleSpanish(511, "arlo", "", 3, -1,  1, "NULL");
        RuleSpanish r12=new RuleSpanish(512, "arla", "", 3, -1,  1, "NULL");
        RuleSpanish r13=new RuleSpanish(513, "erle", "", 3, -1,  1, "NULL");
        RuleSpanish r14=new RuleSpanish(514, "erlo", "", 3, -1,  1, "NULL");
        RuleSpanish r15=new RuleSpanish(515, "erla", "", 3, -1,  1, "NULL");
        RuleSpanish r16=new RuleSpanish(516, "irle", "", 3, -1,  1, "NULL");
        RuleSpanish r17=new RuleSpanish(517, "irlo", "", 3, -1,  1, "NULL");
        RuleSpanish r18=new RuleSpanish(518, "irla", "", 3, -1,  1, "NULL");
        RuleSpanish r19=new RuleSpanish(519, "able", "", 3, -1,  1, "NULL");
        RuleSpanish r20=new RuleSpanish(520, "arse", "", 3, -1,  1, "NULL");
        RuleSpanish r21=new RuleSpanish(521, "irse", "", 3, -1,  1, "NULL");
        RuleSpanish r22=new RuleSpanish(522, "ible", "", 3, -1,  1, "NULL");
        RuleSpanish r23=new RuleSpanish(523, "ción", "", 3, -1,  1, "NULL");
        RuleSpanish r24=new RuleSpanish(524, "sión", "", 3, -1,  1, "NULL");
        RuleSpanish r25=new RuleSpanish(525, "ismo", "", 3, -1,  1, "NULL");
        RuleSpanish r26=new RuleSpanish(526, "ente", "", 3, -1,  1, "NULL");
        RuleSpanish r27=new RuleSpanish(527, "aria", "", 3, -1,  1, "NULL");
        RuleSpanish r28=new RuleSpanish(528, "ario", "", 3, -1,  1, "NULL");
        RuleSpanish r29=new RuleSpanish(529, "ante", "", 3, -1,  1, "NULL");
        RuleSpanish r30=new RuleSpanish(530, "idad", "", 3, -1,  1, "NULL");
        RuleSpanish r31=new RuleSpanish(531, "ada", "", 2, -1,  1, "NULL");
        RuleSpanish r32=new RuleSpanish(532, "ado", "", 2, -1,  1, "NULL");
        RuleSpanish r33=new RuleSpanish(533, "ano", "", 2, -1,  1, "NULL");
        RuleSpanish r34=new RuleSpanish(534, "ana", "", 2, -1,  1, "NULL");
        RuleSpanish r35=new RuleSpanish(535, "era", "", 2, -1,  1, "NULL");
        RuleSpanish r36=new RuleSpanish(536, "ero", "", 2, -1,  1, "NULL");
        RuleSpanish r37=new RuleSpanish(537, "al", "", 1, -1,  1, "NULL");
        RuleSpanish r38=new RuleSpanish(538, "ar", "", 1, -1,  1, "NULL");
        RuleSpanish r39=new RuleSpanish(539, "er", "", 1, -1,  1, "NULL");
        RuleSpanish r40=new RuleSpanish(540, "ir", "", 1, -1,  1, "NULL");
        listaReglas5.addElement(r1);
        listaReglas5.addElement(r2);
        listaReglas5.addElement(r3);
        listaReglas5.addElement(r4);
        listaReglas5.addElement(r5);
        listaReglas5.addElement(r6);
        listaReglas5.addElement(r7);
        listaReglas5.addElement(r8);
        listaReglas5.addElement(r9);
        listaReglas5.addElement(r10);
        listaReglas5.addElement(r11);
        listaReglas5.addElement(r12);
        listaReglas5.addElement(r13);
        listaReglas5.addElement(r14);
        listaReglas5.addElement(r15);
        listaReglas5.addElement(r16);
        listaReglas5.addElement(r17);
        listaReglas5.addElement(r18);
        listaReglas5.addElement(r19);
        listaReglas5.addElement(r20);
        listaReglas5.addElement(r21);
        listaReglas5.addElement(r22);
        listaReglas5.addElement(r23);
        listaReglas5.addElement(r24);
        listaReglas5.addElement(r25);
        listaReglas5.addElement(r26);
        listaReglas5.addElement(r27);
        listaReglas5.addElement(r28);
        listaReglas5.addElement(r29);
        listaReglas5.addElement(r30);
        listaReglas5.addElement(r31);
        listaReglas5.addElement(r32);
        listaReglas5.addElement(r33);
        listaReglas5.addElement(r34);
        listaReglas5.addElement(r35);
        listaReglas5.addElement(r36);
        listaReglas5.addElement(r37);
        listaReglas5.addElement(r38);
        listaReglas5.addElement(r39);
        listaReglas5.addElement(r40);

    }

    /**
     * Inicializa las reglas de tipo 6
     */
    private void inicializaReglas6()
    {
        RuleSpanish r1=new RuleSpanish(601, "isi","i",2, 0, 0, "NULL");
        RuleSpanish r2=new RuleSpanish(602, "ya", "", 1,-1, 0, "NULL");
        RuleSpanish r3=new RuleSpanish(603, "ye", "", 1,-1, 0, "NULL");
        RuleSpanish r4=new RuleSpanish(604, "yo", "", 1,-1, 0, "NULL");
        RuleSpanish r5=new RuleSpanish(605, "yá", "", 1,-1, 0, "NULL");
        RuleSpanish r6=new RuleSpanish(606, "yé", "", 1,-1, 0, "NULL");
        RuleSpanish r7=new RuleSpanish(607, "yó", "", 1,-1, 0, "NULL");
        RuleSpanish r8=new RuleSpanish(608, "eo", "", 1,-1, 0, "NULL");
        RuleSpanish r9=new RuleSpanish(609, "ea", "", 1,-1, 0, "NULL");
        RuleSpanish r10=new RuleSpanish(610, "io", "",1,-1, 0, "NULL");
        RuleSpanish r11=new RuleSpanish(611, "ia", "",1,-1, 0, "NULL");
        RuleSpanish r12=new RuleSpanish(612, "ón", "",1,-1, 0, "NULL");
        RuleSpanish r13=new RuleSpanish(613, "a", "", 0,-1, 0, "NULL");
        RuleSpanish r14=new RuleSpanish(614, "e", "", 0,-1, 0, "NULL");
        RuleSpanish r15=new RuleSpanish(615, "i", "", 0,-1, 0, "NULL");
        RuleSpanish r16=new RuleSpanish(616, "o", "", 0,-1, 0, "NULL");
        RuleSpanish r17=new RuleSpanish(617, "u", "", 0,-1, 0, "NULL");
        RuleSpanish r18=new RuleSpanish(618, "á", "", 0,-1, 0, "NULL");
        RuleSpanish r19=new RuleSpanish(619, "é", "", 0,-1, 0, "NULL");
        RuleSpanish r20=new RuleSpanish(620, "í", "", 0,-1, 0, "NULL");
        RuleSpanish r21=new RuleSpanish(621, "ó", "", 0,-1, 0, "NULL");
        RuleSpanish r22=new RuleSpanish(622, "ú", "", 0,-1, 0, "NULL");
        RuleSpanish r23=new RuleSpanish(623, "e", "", 0,-1, 0, "NULL");
        RuleSpanish r24=new RuleSpanish(624, "é", "", 0,-1, 0, "NULL");
        listaReglas6.addElement(r1);
        listaReglas6.addElement(r2);
        listaReglas6.addElement(r3);
        listaReglas6.addElement(r4);
        listaReglas6.addElement(r5);
        listaReglas6.addElement(r6);
        listaReglas6.addElement(r7);
        listaReglas6.addElement(r8);
        listaReglas6.addElement(r9);
        listaReglas6.addElement(r10);
        listaReglas6.addElement(r11);
        listaReglas6.addElement(r12);
        listaReglas6.addElement(r13);
        listaReglas6.addElement(r14);
        listaReglas6.addElement(r15);
        listaReglas6.addElement(r16);
        listaReglas6.addElement(r17);
        listaReglas6.addElement(r18);
        listaReglas6.addElement(r19);
        listaReglas6.addElement(r20);
        listaReglas6.addElement(r21);
        listaReglas6.addElement(r22);
        listaReglas6.addElement(r23);
        listaReglas6.addElement(r24);

    }

    /**
     * Metodo de comprueba si un caracter es alfanumérico.
     * @param caracter
     * @return boolean.
     */
    private boolean isalpha(char c)
    {
        if ((c >= '0' && c <= '9') ||
            (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            (c=='ñ') || (c=='Ñ'))
//          || (c >= 192 && c <= 255))
            return true;
        else
            return false;
    }

    /**
     * Método que dice si un carácter es vocal.
     * @param caracter.
     * @return boolean. Indica si el caracter dado es vocal.
     */
    private boolean esVocal(char c)
    {
        return (('a'==(c)||'e'==(c)||'i'==(c)||'o'==(c)||'u'==(c)||'á'==(c)||'é'==(c)||'í'==(c)||'ó'==(c)||'ú'==(c)));
    }

    /**
     * Indica si un carácter es vocal,w,x ó y.
     * @param caracter.
     * @return boolean. Indica si el caracter dado es vocal.
     */
    private boolean esVocal_wxy(char c)
    {
        return (('a'==(c)||'e'==(c)||'i'==(c)||'o'==(c)||'u'==(c)||'á'==(c)||'é'==(c)||'í'==(c)||'ó'==(c)||'ú'==(c)||'w'==(c)||'x'==(c)||'y'==(c)));
    }
    /**
     * Dice si un carácter es vocal ó y.
     * @param caracter.
     * @return boolean. Indica si el caracter dado es vocal.
     */
    private boolean esVocal_y(char c)
    {
        return (('a'==(c)||'e'==(c)||'i'==(c)||'o'==(c)||'u'==(c)||'á'==(c)||'é'==(c)||'í'==(c)||'ó'==(c)||'ú'==(c)||'y'==(c)));
    }
    /**
     * Método que calcula el numero de veces que se produce un cambio de Vocal a Consonante
     * @param String palabra. Palabra de la que se quiere conocer su longitud.
     * @return Longitud de la palabra
     */
    private int longPalabra()
    {
        int resultado=0;
        int estado=0;
        int pos=0;
        char car=' ';

        while(pos < palabra.length())
        {
            car=palabra.charAt(pos);
            switch(estado)
            {
                case 0:
                    if (esVocal(car))
                        estado=1;
                    else
                        estado=2;
                    break;
                case 1:
                    if (esVocal(car))
                        estado=1;
                    else
                        estado=2;
                    if (estado==2)
                        resultado=resultado+1;
                    break;
                case 2:
                    if (esVocal(car) || car == 'y')
                        estado=1;
                    else
                        estado=2;
                    break;
            }
            pos=pos+1;
        }
        return resultado;
    }


    /**
     * Método que dice si la palabra contiene una vocal.
     * @param String. Palabra
     * @return boolean.
     */
      private boolean contieneVocal()
      {
         for (int i=0 ; i < palabra.length(); i++ )
            if ( i > 0 )
            {
               if (esVocal_y(palabra.charAt(i)))
                  return true;
            }
         return false;
      }

    /**
     * Devuelve true si la palabra termina en CVC (Consonante Vocal Consonante)
     * @param String palabra
     * @return boolean
     */

     private boolean terminaEnCVC()
     {
        int longitud;
        char ultimo;
        char penultimo;
        char antepenultimo;

        longitud=palabra.length();
        if (longitud < 3)
            return false;
        else
        {
            ultimo=palabra.charAt(longitud-1);
            penultimo=palabra.charAt(longitud-2);
            antepenultimo=palabra.charAt(longitud-3);
            return (!esVocal_wxy(ultimo) && esVocal_y(penultimo) && !esVocal(antepenultimo));
        }
     }

    /**
     * Método que indica si se debe borrar la última E.
     * @param String palabra
     * @return boolean
     */
     private boolean eliminaE()
     {
        return((longPalabra() == 1) && !terminaEnCVC());
     }

    /**
     * Comprobamos si se cumple una condición para aplicar la regla.
     * @param String regla, String palabra
     * @return boolean. Indica si se cumple la regla.
     */
    private boolean seCumpleRegla(String regla)
    {
        boolean seCumple=false;
        if(regla.equals("ContieneVocal"))
        {
            seCumple=(contieneVocal());
        }
        else if (regla.equals("eliminaE"))
        {
            seCumple=(eliminaE());
        }
        else if (regla.equals("NULL"))
        {
            seCumple=true;
        }
        return seCumple;
    }

    /**
     * Sustituye el final de la palabra aplicando todas las reglas en cadena
     * @param String palabra, Lista de Reglas
     * @return int. Ultima regla aplicada
     */
     private int sustituirFinal(Vector reglas)
     {
        int idRegla=0;
        Enumeration listaReglas=reglas.elements();
        RuleSpanish regl;
        String tmp=new String("");
        String ending=new String("");
        int ini_ending=0;
        while(listaReglas.hasMoreElements())
        {
            //Cargamos la nueva regla en regl
            regl=(RuleSpanish)listaReglas.nextElement();
            //Asociamos el identificador de la regla a idRegla
            idRegla=regl.obtenerId();
            //end apunta a la ultima posicion de la raizPalabra
            ini_ending=end-regl.obtenerOldOffset();
            if(ini_ending>0)
                ending=palabra.substring(ini_ending-1,palabra.length());
            if(!ending.equals(""))
            {
                if (ending.equals(regl.obtenerOldEnd()))
                {
                    tmp=ending;
                    if(regl.obtenerMinRootSize() < longPalabra())
                    {
                        if (seCumpleRegla(regl.obtenerCondicion()))
                        {
                            //Sustituimos la terminacion antigua por la nueva.
                            palabra=palabra.substring(0,ini_ending-1)+regl.obtenerNewEnd();
                            end=ini_ending+regl.obtenerNewOffset();
                            break;
                        }
                    }
                    ending=tmp;
                }
            }
        }
        return idRegla;
     }

     /**
     * Calcula la raiz de una palabra.
     * @param palabra que usamos para extraer su raíz.
     * @return raiz de la palabra
     */
     public String raiz(String palabraRecibida)
     {
        int idRegla;
        char car=' ';
        if(palabraRecibida.equals(""))
        {
             System.out.println("Extractor de raíces.");
             System.out.println("");
             System.out.println("   palabra");
             System.out.println("       Palabra de la que se quiere extraer su raíz.");
        }
        palabra=palabraRecibida;
     /*   for(int pos=0;pos<palabra.length();pos++)
        {
            car=palabra.charAt(pos);
            if (!isalpha(car))
                if(!(car=='á' || car=='é' || car=='í' || car=='ó' || car=='ú'))
                {
		    System.out.println(palabra);
                    return palabra;
                }
        }*/
        end=palabra.length();//Contiene la posicion del último caracter de la palabra
        palabra=palabra.toLowerCase();
        idRegla=sustituirFinal(listaReglas1);
        idRegla=sustituirFinal(listaReglas2);
        idRegla=sustituirFinal(listaReglas3);
        idRegla=sustituirFinal(listaReglas4);
        idRegla=sustituirFinal(listaReglas5);
        idRegla=sustituirFinal(listaReglas6);
        raizPalabra=palabra;
        return raizPalabra;
     }

}
