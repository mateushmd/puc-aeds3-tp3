package pucflix.view;

import pucflix.entity.Actor;
import pucflix.entity.Show;
import pucflix.model.ActorFile;
import pucflix.model.ShowFile;
import pucflix.model.PostingsList;
import pucflix.aeds3.ListaInvertida;

public class ActorView extends View {
    private ActorFile aFile;
    private ShowFile sFile;
    private PostingsList pList;

    public ActorView(Prompt prompt, ActorFile aFile, ShowFile sFile, PostingsList pList) throws Exception {
        super(prompt);
        this.aFile = aFile;
        this.sFile = sFile;
        this.pList = pList;
    }

    @Override
    public String getName() {
        return "Atores";
    }

    @Override
    public String getPrompt(int depth) throws Exception {
        return "1) Incluir\n" +
                "2) Ler\n" +
                "3) Buscar\n" +
                "4) Alterar\n" +
                "5) Excluir\n" +
                "6) Adicionar série\n" +
                "7) Remover série\n" +
                "8) Listar séries\n";
    }

    @Override
    public void eval(int input, int depth) throws Exception {
        switch (input) {
            case 1: { // Incluir
                String name = prompt.askForInput("Nome do ator: ");
                Actor actor = new Actor(name);
                int id = aFile.create(actor);
                pList.add(actor.getName(), id);
                System.out.println("Ator criado com sucesso.");
                break;
            }
            case 2: { // Ler
                String search = prompt.askForInput("Nome: ");
                Actor[] actors = aFile.readByName(search);

                if (actors == null || actors.length == 0) {
                    System.out.println("Nenhum ator encontrado.");
                    break;
                }

                if (actors.length == 1) {
                    System.out.println(actors[0]);
                    break;
                }

                for (int i = 0; i < actors.length; i++)
                    System.out.println((i + 1) + ") " + actors[i].getName());

                int n = 0;
                boolean valid = false;

                while (!valid) {
                    try {
                        n = Integer.parseInt(prompt.askForInput("Número: "));
                        if (n < 1 || n > actors.length)
                            throw new Exception();
                        valid = true;
                    } catch (Exception e) {
                        System.out.println("Insira um número válido.");
                    }
                }

                System.out.println(actors[n - 1]);
                break;
            }
            // Testar lista invertida
            case 3: {
                String search = prompt.askForInput("Nome: ");

                int[] ids = pList.search(search);

                if (ids.length == 0) {
                    System.out.println("Nenhum ator encontrado");
                    break;
                }

                Actor[] actors = new Actor[ids.length];

                for (int i = 0; i < ids.length; i++) {
                    actors[i] = aFile.read(ids[i]);
                    System.out.println((i + 1) + " - " + actors[i].getName());
                }

                int n = 0;
                boolean valid = false;

                while (!valid) {
                    try {
                        int select = Integer.parseInt(prompt.askForInput("Número: "));
                        if (select < 1 || select > actors.length + 1)
                            throw new Exception();

                        n = select;
                        valid = true;
                    } catch (Exception ex) {
                        System.out.println("Insira um número válido.");
                    }
                }

                System.out.println(actors[n - 1]);
                break;
            }
            case 4: { // Alterar
                String search = prompt.askForInput("Nome: ");
                Actor[] actors = aFile.readByName(search);
                int n = 0;

                while (actors == null || actors.length == 0) {
                    search = prompt.askForInput("Nenhum ator encontrado, tente novamente: ");
                    actors = aFile.readByName(search);
                }

                if (actors.length > 1) {
                    for (int i = 0; i < actors.length; i++)
                        System.out.println((i + 1) + ") " + actors[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            n = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (n < 1 || n > actors.length)
                                throw new Exception();
                            valid = true;
                            --n;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Actor actor = actors[n];
                String oldName = actor.getName();
                String name = prompt.askForInput("Novo nome (vazio para manter): ");

                if (!name.isEmpty())
                    actor.setName(name);

                aFile.update(actor);
                pList.delete(oldName, actor.getID());
                pList.add(name, actor.getID());
                System.out.println("Ator atualizado com sucesso.");
                break;
            }
            case 5: { // Excluir
                String search = prompt.askForInput("Nome: ");
                Actor[] actors = aFile.readByName(search);
                int n = 0;

                while (actors == null || actors.length == 0) {
                    search = prompt.askForInput("Nenhum ator encontrado, tente novamente: ");
                    actors = aFile.readByName(search);
                }

                if (actors.length > 1) {
                    for (int i = 0; i < actors.length; i++)
                        System.out.println((i + 1) + ") " + actors[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            n = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (n < 1 || n > actors.length)
                                throw new Exception();
                            valid = true;
                            --n;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Actor actor = actors[n];

                Show[] actorShows = sFile.readByActor(actor.getID());

                if (actorShows != null && actorShows.length > 0) {
                    System.out.println("Não foi possível remover o ator, ele está associado a uma ou mais séries.");
                } else if (aFile.delete(actor.getID())) {
                    System.out.println("Ator removido com sucesso.");
                } else {
                    System.out.println("Não foi possível remover o ator.");
                }

                pList.delete(actor.getName(), actor.getID());

                break;
            }
            case 6: { // Adicionar série ao ator
                String name = prompt.askForInput("Nome do ator: ");
                Actor[] actors = aFile.readByName(name);
                int n = 0;

                if (actors == null || actors.length == 0) {
                    System.out.println("Ator não encontrado.");
                    break;
                }

                if (actors.length > 1) {
                    for (int i = 0; i < actors.length; i++)
                        System.out.println((i + 1) + ") " + actors[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            n = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (n < 1 || n > actors.length)
                                throw new Exception();
                            valid = true;
                            --n;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Actor actor = actors[n];

                String showName = prompt.askForInput("Nome da série: ");
                Show[] shows = sFile.read(showName);
                int m = 0;

                if (shows == null || shows.length == 0) {
                    System.out.println("Série não encontrada.");
                    break;
                }

                if (shows.length > 1) {
                    for (int i = 0; i < shows.length; i++)
                        System.out.println((i + 1) + ") " + shows[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            m = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (m < 1 || m > shows.length)
                                throw new Exception();
                            valid = true;
                            --m;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Show show = shows[m];

                if (aFile.addShow(actor.getID(), show.getID()) && sFile.addActor(show.getID(), actor.getID())) {
                    System.out.println("Série adicionada ao ator com sucesso.");
                } else {
                    System.out.println("Não foi possível adicionar.");
                }

                break;
            }
            case 7: { // Remover série
                String name = prompt.askForInput("Nome do ator: ");
                Actor[] actors = aFile.readByName(name);
                int n = 0;

                if (actors == null || actors.length == 0) {
                    System.out.println("Ator não encontrado.");
                    break;
                }

                if (actors.length > 1) {
                    for (int i = 0; i < actors.length; i++)
                        System.out.println((i + 1) + ") " + actors[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            n = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (n < 1 || n > actors.length)
                                throw new Exception();
                            valid = true;
                            --n;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Actor actor = actors[n];

                String showName = prompt.askForInput("Nome da série: ");
                Show[] shows = sFile.read(showName);
                int m = 0;

                if (shows == null || shows.length == 0) {
                    System.out.println("Série não encontrada.");
                    break;
                }

                if (shows.length > 1) {
                    for (int i = 0; i < shows.length; i++)
                        System.out.println((i + 1) + ") " + shows[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            m = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (m < 1 || m > shows.length)
                                throw new Exception();
                            valid = true;
                            --m;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Show show = shows[m];

                if (aFile.removeShow(actor.getID(), show.getID()) && sFile.removeActor(show.getID(), actor.getID())) {
                    System.out.println("Série removida com sucesso.");
                } else {
                    System.out.println("Não foi possível remover.");
                }

                break;
            }
            case 8: { // Listar séries de um ator
                String name = prompt.askForInput("Nome do ator: ");
                Actor[] actors = aFile.readByName(name);
                int n = 0;

                if (actors == null || actors.length == 0) {
                    System.out.println("Ator não encontrado.");
                    break;
                }

                if (actors.length > 1) {
                    for (int i = 0; i < actors.length; i++)
                        System.out.println((i + 1) + ") " + actors[i].getName());

                    boolean valid = false;
                    while (!valid) {
                        try {
                            n = Integer.parseInt(prompt.askForInput("Escolha: "));
                            if (n < 1 || n > actors.length)
                                throw new Exception();
                            valid = true;
                            --n;
                        } catch (Exception e) {
                            System.out.println("Número inválido.");
                        }
                    }
                }

                Actor actor = actors[n];

                for (Show show : sFile.readByActor(actor.getID())) {
                    System.out.println("- " + show.getName());
                }

                break;
            }
        }
    }
}
