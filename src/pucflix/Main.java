package pucflix;

import pucflix.aeds3.*;
import pucflix.view.*;
import pucflix.model.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {

        try {
            Prompt prompt = new Prompt();
            prompt.start();
            prompt.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        /*
         * try {
         * File f = new File("./dados");
         * if (!f.exists())
         * f.mkdir();
         * PostingsList l = new PostingsList(4, "./dados/dicionario.listaenv.bd",
         * "./dados/blocos.listaenv.bd");
         * l.add("Agendar reunião com a equipe de atendimentos", 1);
         * l.add("Enviar relatório de desempenho da equipe para a diretoria", 2);
         * l.add("Verificar e-mails da diretoria e e-mails da equipe", 3);
         * l.add("Enviar e-mails sobre a reunião para os clientes", 4);
         * l.add("Elaborar relatório de atendimentos aos clientes", 5);
         * for (int id : l.search("e-mails reunião equipe")) {
         * System.out.println(id);
         * }
         * System.out.println();
         * l.delete("Agendar reunião com a equipe de atendimentos", 1);
         * for (int id : l.search("e-mails reunião equipe")) {
         * System.out.println(id);
         * }
         * } catch (Exception ex) {
         * System.err.println(ex.getMessage());
         * }
         */
    }
}
