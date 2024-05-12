package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {



    // Constantes são em caps locks e final pois NÃO muda
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=c8a75190";

    // Ao invés de dar estes new no meio do código .. dou ele no inicio das classes
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private Scanner leitura = new Scanner(System.in);

    private List<DadosSerie> dadosSeries = new ArrayList<>();


    public void exibeMenu() {
        var opcao = -1;
        while ( opcao != 0 ) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar Séries Buscadas
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        dadosSeries.add(dados);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);
    }

    private void listarSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
        series = dadosSeries.stream()
                        .map(d -> new Serie(d))
                                .collect(Collectors.toList());

        //dadosSeries.forEach(System.out::println);
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }


    /*
    public void exibeMenu(){
        System.out.println("Digite o nome da séria");
        var nomeSerie = leitura.nextLine();
        String json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ","+")+API_KEY);
        DadosSerie dados = conversor.obterDados(json,DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i<=dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO+nomeSerie.replace(" ","+")+API_KEY+"&season=" + i);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
//
//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        // never have i ever

        // Usando a função lambda -> e os parametros e fazemo então um for dentro de outro for
        // apenas com uma linha, simples rápido e pratico.

        temporadas.forEach((t -> t.episodios().forEach((e -> System.out.println(e.titulo())))));

        // temporadas.forEach(t -> System.out.println(t));
        // a linha acima usando função lambda pode ser substituida
        // pela linha de comando abaixo usando :: quando for uma iteração unica de um forEach simples
        // temporadas.forEach(System.out::println);

        System.out.println("\nTop 5 episódios");

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        // criamos uma lista com os dados de cada episódio de todas as temporadas
        // usando stream encadeados  e collect e vamos agora ordenar por avaliacao
        // de forma decrescente e imprimir os 5 primeiros maiores numeros de avaliação
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        // CRIANDO UMA LISTA DE EPISÓDIOS DE ACORDO COM A CLASSE QUE CONTRUÍMOS
        // E TRANSFORMANDO OS DADOS DA LISTA COM O MAP E INICIALIZANDO O NOSSO
        // CONSTRUTOR DE NOSSA CLASSE E CRIAMOS UMA COLETION LIST
        List<Episodio> episodios = temporadas.stream()
                .flatMap( t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        // BUSCANDO UM EPISODIO A PARTIR DE UMA PARTE DE SEU NOME E REOTORNANDO A TEMPORADA DELE
        System.out.println("Digite um trecho do título do episódio ");
        var trechoDoTitulo = leitura.nextLine();

        Optional<Episodio> espisodioEncontrado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains((trechoDoTitulo.toUpperCase())))
                .findFirst(); // vai buscar a primeira rferencia digitada que encontrar


        if(espisodioEncontrado.isPresent()) {
            System.out.println("Episodio encontrado! ");
            System.out.println("Temporada: " + espisodioEncontrado.get().getTemporada());
            System.out.println(("Titulo: ")+ espisodioEncontrado.get().getTitulo());
        } else {
            System.out.println("Episodio não encontrado!");
        }

        // BUSCANDO OS EPISODIOS A PARTIR DO ANO INFORMADO. TRABALHANDO COM DATA E FORMATANDO AS MESMA
        /// *************************************************************************************** //
        System.out.println("A partir de que ano vc deseja ver a lista de episodios: ");
        var ano = leitura.nextInt();
        leitura.nextLine();
        LocalDate dataBusca = LocalDate.of(ano,1,1);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter( e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getTemporada() +
                                " Episódio: " + e.getTitulo()+
                                " Data lançamento: " + e.getDataLancamento().format(formatador)
                ));

        // utilizamos um MAP pois estamos trabalhando com os dados INTEGER E DOUBLE de propriedades diferentes
        /// Agrupamos todos os episodios para cada temporada equivalente e fizemo uma media das avaliações  //
        //  Daquela temporada e depois imprimimos o resultado.
        Map< Integer , Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0 )
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        // aqui utilizamos o método DoubleSumarySatistics que traz as estatísticas de
        // acordo com o summarizingDouble() especificado
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println(est);
    }
    */

}
