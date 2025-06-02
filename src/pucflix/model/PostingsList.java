package pucflix.model;

import java.io.IOError;
import java.io.IOException;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import pucflix.aeds3.ListaInvertida;
import pucflix.aeds3.ElementoLista;

public class PostingsList extends ListaInvertida {
    private class Term {
        public String value;
        public int occurrence;

        public Term(String str) {
            value = str;
            occurrence = 1;
        }

        public int compareTo(String other) {
            return value.compareTo(other);
        }
    }

    private class EntityResult {
        public int id;
        public float value;

        public EntityResult(int id) {
            this.id = id;
        }
    }

    private static final String ACCENTUATION = "áàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÔÖÚÙÛÜÇ";
    private static final String NO_ACCENTUATION = "aaaaaeeeeiiiiooooouuuucAAAAAEEEEIIIIOOOOOUUUUC";

    private String[] stopwords = {
            "de", "a", "o", "que", "e", "do", "da", "em", "um", "para", "e", "com", "nao", "uma", "os",
            "no", "se", "na", "por", "mais", "as", "dos", "como", "mas", "foi", "ao", "ele", "das", "tem",
            "a", "seu", "sua", "ou", "ser", "quando", "muito", "ha", "nos", "ja", "esta", "eu", "tambem",
            "so", "pelo", "pela", "ate", "isso", "ela", "entre", "era", "depois", "sem", "mesmo", "aos",
            "ter", "seus", "quem", "nas", "me", "esse", "eles", "estao", "voce", "tinha", "foram", "essa",
            "num", "nem", "suas", "meu", "as", "minha", "tem", "numa", "pelos", "elas", "havia", "seja",
            "qual", "sera", "nos", "tenho", "lhe", "deles", "essas", "esses", "pelas", "este", "fosse",
            "dele", "tu", "te", "voces", "vos", "lhes", "meus", "minhas", "teu", "tua", "teus", "tuas",
            "nosso", "nossa", "nossos", "nossas", "dela", "delas", "esta", "estes", "estas", "aquele",
            "aquela", "aqueles", "aquelas", "isto", "aquilo", "estou", "esta", "estamos", "estao", "estive",
            "esteve", "estivemos", "estiveram", "estava", "estavamos", "estavam", "estivera", "estiveramos",
            "esteja", "estejamos", "estejam", "estivesse", "estivessemos", "estivessem", "estiver",
            "estivermos", "estiverem", "hei", "ha", "havemos", "hao", "houve", "houvemos", "houveram",
            "houvera", "houveramos", "haja", "hajamos", "hajam", "houvesse", "houvessemos", "houvessem",
            "houver", "houvermos", "houverem", "houverei", "houvera", "houveremos", "houverao", "houveria",
            "houveriamos", "houveriam", "sou", "somos", "sao", "era", "eramos", "eram", "fui", "foi",
            "fomos", "foram", "fora", "foramos", "seja", "sejamos", "sejam", "fosse", "fossemos", "fossem",
            "for", "formos", "forem", "serei", "sera", "seremos", "serao", "seria", "seriamos", "seriam",
            "tenho", "tem", "temos", "tem", "tinha", "tinhamos", "tinham", "tive", "teve", "tivemos",
            "tiveram", "tivera", "tiveramos", "tenha", "tenhamos", "tenham", "tivesse", "tivessemos",
            "tivessem", "tiver", "tivermos", "tiverem", "terei", "tera", "teremos", "terao", "teria",
            "teriamos", "teriam"
    };

    public PostingsList(int n, String nd, String nc) throws Exception {
        super(n, nd, nc);// n => quantidade de dados por bloco
    }

    private String normalize(String s) {
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();

        for (char c : chars) {
            int idx = ACCENTUATION.indexOf(c);
            if (idx >= 0)
                sb.append(Character.toLowerCase(NO_ACCENTUATION.charAt(idx)));
            else
                sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    private boolean isStopWord(String str) {
        for (String s : stopwords) {
            if (str.equals(s))
                return true;
        }

        return false;
    }

    public void add(String str, int id) throws Exception {
        String[] strs = str.split(" ");
        Term[] terms = new Term[strs.length];
        int size = 0; // numero total de termos
        int stops = 0;// numero de stop words

        for (String s : strs)// para cada string S em strs
        {
            String norm = normalize(s);// vai tirar cada acento e deixar a string toda em lowercase

            if (norm.isEmpty())
                continue;
            if (isStopWord(norm))// incrementa a entidade stops caso seja encontrada uma stopword
            {
                stops++;
                continue;
            }

            int j = size - 1;
            while (j >= 0 && terms[j].compareTo(norm) > 0) {
                terms[j + 1] = terms[j];
                j--;
            }

            if (j >= 0 && terms[j].compareTo(norm) == 0) {
                terms[j].occurrence++;
            } else {
                terms[j + 1] = new Term(norm);
                size++;
            }
        }

        for (int i = 0; i < size; i++) {
            create(terms[i].value,
                    new ElementoLista(
                            id,
                            (float) terms[i].occurrence / (strs.length - stops)));
        }
    }

    public boolean delete(String full, int id) throws Exception {
        String[] strs = normalize(full).split(" ");

        for (String s : strs) {
            if (isStopWord(s))
                continue;

            if (!super.delete(s, id))
                return false;
        }

        decrementaEntidades();

        return true;
    }

    /*
     * public double IDF(String s) {
     * String normalized = normalize(s);
     * String[] strs = normalized.split(" ");
     * StringBuilder sb = new StringBuilder();
     * 
     * for (String word : strs) {
     * if (!isStopWord(word)) {
     * sb.append(word);
     * sb.append(' ');
     * }
     * }
     * 
     * String result = sb.toString().trim(); // string normalizada
     * 
     * ElementoLista[] elist = null;
     * 
     * try { // busca por todas as ocorrencias daquela string
     * elist = read(result);
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * 
     * double f = 0.0;
     * 
     * try {// calcula e retorna o IDF
     * f = numeroEntidades() / elist.length;
     * return f;
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * 
     * return f;
     * 
     * }
     */

    public int[] search(String s) throws Exception {
        String[] strs = normalize(s).split(" ");
        ArrayList<EntityResult> entities = new ArrayList<EntityResult>();
        int entityCount = numeroEntidades();

        for (String str : strs) {
            if (isStopWord(str))
                continue;

            ElementoLista[] result = super.read(str);

            float idf = (float) entityCount / result.length;

            for (ElementoLista el : result) {
                addOrUpdate(entities, el.getId(), idf * el.getFrequencia());
            }
        }

        Collections.sort(entities, new Comparator<EntityResult>() {
            @Override
            public int compare(EntityResult e1, EntityResult e2) {
                return Float.compare(e2.value, e1.value);
            }
        });

        int[] ids = new int[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            ids[i] = entities.get(i).id;
        }

        return ids;
    }

    private void addOrUpdate(ArrayList<EntityResult> entities, int id, float idfXFreq) {
        int idx = -1;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).id == id) {
                idx = i;
                break;
            }
        }

        EntityResult entity;
        if (idx == -1) {
            entity = new EntityResult(id);
            entities.add(entity);
        } else
            entity = entities.get(idx);

        entity.value += idfXFreq;
    }
}
