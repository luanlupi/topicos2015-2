package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import java.util.Vector;


/**
 * Created by Luan on 03/08/2015.
 */
public class TelaJogo extends TelaBase {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
    private  Stage palcoInformacoes;
    private BitmapFont fonte;
    private Label lbPontuacao;
    private Label lbGameOver;
    private Image jogador;
    private Texture testuraJogador;
    private Texture testuraJogadorDireito;
    private Texture testuraJogadorEsquerdo;
    private  boolean indoDireita;
    private boolean indoEsquerdo;
    private boolean atirando;
    private Array<Image> tiros = new Array<Image>();
    private Texture testuraTiros;
    private Texture texturaMeteoro1;
    private Texture texturaMeteoro2;
    private Array<Image>meteoros1 = new Array<Image>();
    private Array<Image>meteoros2 = new Array<Image>();

    private Array<Texture> texturasExplosao = new Array<Texture>();
    private Array<Explosao> explosaes = new Array<Explosao>();

    private Sound somTiro;
    private Sound somExplosao;
    private Sound somGameOver;
    private Music musicaFundo;


    /**
     * Construtor padrao da tela de jogo
     * @param game referencia para classe principal
     */
    public TelaJogo (MainGame game){
        super(game);
    }

    /**
     * Chamado quando a tela e exibida
     */
    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
        palco = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));
        palcoInformacoes = new Stage(new FillViewport(camera.viewportWidth, camera.viewportHeight, camera));

        initSons();
        initTesturas();
        initfonte();
        initInformacoes();
        initJogador();

    }
    private void initSons() {
        somTiro = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.mp3"));
        somExplosao = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.mp3"));
        somGameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.mp3"));
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("sounds/background.mp3"));
        musicaFundo.setLooping(true);
    }

    private void initTesturas() {
        testuraTiros = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");

        for(int i= 1;i <= 17; i++){
            Texture text = new Texture("sprites/explosion-" + i +".png");
            texturasExplosao.add(text);
        }
    }

    /**
     * Instancia os objtos do jogador e adiciona o palco
     */
    private void initJogador() {
        testuraJogador = new Texture("sprites/player.png");
        testuraJogadorDireito = new Texture("sprites/player-right.png");
        testuraJogadorEsquerdo = new Texture("sprites/player-left.png");
        jogador = new Image(testuraJogador);
        float x = camera.viewportWidth / 2 - jogador.getWidth() / 2;
        float y = 15;
        jogador.setPosition(x, y);
        palco.addActor(jogador);


    }

    /**
     * instancia as informacoes escritas na tela
     */
    private void initInformacoes() {
        Label.LabelStyle lbEstilo = new Label.LabelStyle();
        lbEstilo.font = fonte;
        lbEstilo.fontColor = Color.WHITE;

        lbPontuacao = new Label("0 pontos", lbEstilo);
        palco.addActor(lbPontuacao);

        lbGameOver = new Label("Game Over" , lbEstilo);
        lbGameOver.setVisible(false);
        palcoInformacoes.addActor(lbGameOver);
    }

    /**
     * Instancia os objetos de fontes
     */

    private void initfonte() {
        FreeTypeFontGenerator generator =
                new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.color = Color.WHITE;
        param.size = 24;
        param.shadowOffsetX = 1;
        param.shadowOffsetY = 1;
        param.shadowColor = Color.BLUE;

        fonte = generator.generateFont(param);

        generator.dispose();
    }

    /**
     * chamado a todo quadro de atualizacao do jogo(fps)
     * @param delta tempo entre um quadro e outro(em segundo)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - lbPontuacao.getPrefHeight() - 20);
        lbPontuacao.setText(pontuacao + " pontos");

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getPrefWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver == true);


        atualizarExplosoes(delta);
        if(gameOver == false) {
            if(!musicaFundo.isPlaying()) //se  nao esta tocando
                musicaFundo.play(); // incluir musica

            capturaTeclas();
            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMateoros(delta);
            detectarColisoes(meteoros1, 5);
            detectarColisoes(meteoros2, 15);
        }else {
            if(musicaFundo.isPlaying()) // se esta tocando
            musicaFundo.stop();  //inicia a musica
            reiniciarJogo();
        }
        //atualiza a situacao do palco

        palco.act(delta);
        //desenha o palco na tela
        palco.draw();
        //desenha o palco de informacoes
        palcoInformacoes.act(delta);
        palcoInformacoes.draw();
    }

    /**
     * Verfica se o ususario pressionou Enter para reinicar o Jogo
     */

    private void reiniciarJogo() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            Preferences preferences = Gdx.app.getPreferences("spaceInvaders");
            int pontuacaoMaxima = preferences.getInteger("pontuacao_maxima", 0);
            //verifica se nova pontuacao e a maior que a pontuacao maxima
            if(pontuacao > pontuacaoMaxima){
                preferences.putInteger("pontuacao_maxima", pontuacao);
                preferences.flush();
            }
            //volta para a tela de menu
            game.setScreen(new TelaMenu(game));
        }

    }

    private void atualizarExplosoes(float delta) {
        for(Explosao explosao : explosaes) {
            //verifica se a explosao chegou ao fim
            if(explosao.getEstagio()>= 16){
                //chegou ao fim
                explosaes.removeValue(explosao, true);//remove a explosao do array
                explosao.getAtor().remove();//remove o ator do palco
            }else {
                //ainda nao chegou ao fim
                explosao.atulizar(delta);
            }
        }
    }

    private Rectangle recJogador  = new Rectangle();
    private Rectangle recTiros  = new Rectangle();
    private Rectangle recMeteoro  = new Rectangle();
    private  int pontuacao = 0;
    private  boolean gameOver = false;

    private void detectarColisoes(Array<Image> meteoros, int valePonto) {
        recJogador.set(jogador.getX(), jogador.getY(), jogador.getWidth(), jogador.getImageHeight());
        //detecta colisoes com os tiros
        for (Image meteoro : meteoros){
            recMeteoro.set(meteoro.getX(),meteoro.getY(), meteoro.getWidth(), meteoro.getHeight());

            for (Image tiro : tiros) {
                recTiros.set(tiro.getX(), tiro.getY(), tiro.getWidth(), tiro.getImageHeight());
                if(recMeteoro.overlaps(recTiros)){
                    //aki ocorre uma colisao do tiro com o meteoro 1
                    pontuacao += valePonto;
                    tiro.remove(); // remove  o palco
                    tiros.removeValue(tiro, true);// remove da lista
                    meteoro.remove();//remove do palco
                    meteoros.removeValue(meteoro, true);//remove da lista
                    criarExplosao(meteoro.getX() + meteoro.getWidth(), meteoro.getY() + meteoro.getHeight() / 2);
                    somExplosao.play();
                }
            }
            //detecta colisao com o player
            if(recJogador.overlaps(recMeteoro)){
                //ocoorre colisao do jogador com meteoro 1
                gameOver = true;
                somGameOver.play();
            }

        }

    }

    /**
     * criar a explosao na posicao x e y
     * @param x
     * @param y
     */

    private void criarExplosao(float x, float y) {
        Image ator = new Image(texturasExplosao.get(0));
        ator.setPosition(x - ator.getWidth() / 2,
                y - ator.getHeight() / 2);
        palco.addActor(ator);

        Explosao explosao = new Explosao(ator, texturasExplosao);
        explosaes.add(explosao);

    }

    private void atualizarMateoros(float delta) {
        int qtdMeteoros =  meteoros1.size + meteoros2.size; // retorna quantidade de meteoros criados
        if (qtdMeteoros < 15) {

            int tipo = MathUtils.random(1, 4); //retorna 1 ou 2  alertoreamento
            if (tipo == 1) {
                //criar meteoro 1
                Image meteoro = new Image(texturaMeteoro1);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros1.add(meteoro);
                palco.addActor(meteoro);
            } else  if (tipo == 2){
                //criar meteoro 2
                Image meteoro = new Image(texturaMeteoro2);
                float x = MathUtils.random(0, camera.viewportWidth - meteoro.getWidth());
                float y = MathUtils.random(camera.viewportHeight, camera.viewportHeight * 2);
                meteoro.setPosition(x, y);
                meteoros2.add(meteoro);
                palco.addActor(meteoro);
            }
        }
            float velocidade1 = 100; // 200 pixel por segundo
            for (Image meteoro : meteoros1) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade1 * delta;
                meteoro.setPosition(x, y);
                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); //remove do palco
                    meteoros1.removeValue(meteoro, true); // remove da lista
                    pontuacao = pontuacao - 30;
                }
            }
            float velocidade2 = 150; // 200 pixel por segundo
            for (Image meteoro : meteoros2) {
                float x = meteoro.getX();
                float y = meteoro.getY() - velocidade2 * delta;
                meteoro.setPosition(x, y);
                if (meteoro.getY() + meteoro.getHeight() < 0) {
                    meteoro.remove(); //remove do palco
                    meteoros2.removeValue(meteoro, true); // remove da lista
                    pontuacao = pontuacao - 60;

                }
            }

    }

    private final float MAX_INTERVALO_TIROS =0.3f; //minimo de tempo entre os tiros.
    private float intervaloTiros = 0; //tempo acumulado entre os tiros

    private void atualizarTiros(float delta) {
        //cria um novo tiro se necessario
        if (atirando) {
            //verifica seo tempo minimo foi atingido
            intervaloTiros = intervaloTiros + delta; // acumulo o tempo percorrido
            if (intervaloTiros >= MAX_INTERVALO_TIROS) {
                Image tiro = new Image(testuraTiros);
                float x = jogador.getX() + jogador.getWidth() / 2 - tiro.getWidth() / 2;
                float y = jogador.getY() + jogador.getHeight();
                tiro.setPosition(x, y);
                tiros.add(tiro);
                palco.addActor(tiro);
                intervaloTiros = 0;
                somTiro.play();
            }

        }
        float velocidade = 200; //velocidade de movimentacao do tiro
        //percorre todos os tiros exixstente
        for (Image tiro : tiros) {
            //movimento o tiro em direcao ao topo
            float x = tiro.getX();
            float y = tiro.getY() + velocidade * delta;
            tiro.setPosition(x, y);
            //remove os tiros que sairam da tela
            if(tiro.getY() > camera.viewportWidth) {
                tiros.removeValue(tiro, true);
                tiro.remove();

            }

        }
    }

    /**
     * atualizar a posicao do jogador
     * @param delta
     */
    private void atualizarJogador(float delta) {
        float velocidade = 200; //velocidade de movimento do jogador
        if (indoDireita) {
            //verifica se o jogador esta dentro da tela
            if (jogador.getX() < camera.viewportWidth - jogador.getWidth()) {
                float x = jogador.getX() + velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);

            }
        }
        if (indoEsquerdo) {
            //verifica se o jogador esta dentro da tela
            if (jogador.getX() > 0) {
                float x = jogador.getX() - velocidade * delta;
                float y = jogador.getY();
                jogador.setPosition(x, y);
            }
        }
        if(indoDireita){
            //trocar imagem direita
          jogador.setDrawable(new SpriteDrawable(new Sprite(testuraJogadorDireito)));
        }   else if(indoEsquerdo){
            //trocar imagem da esquerda
            jogador.setDrawable(new SpriteDrawable(new Sprite(testuraJogadorEsquerdo)));
        }   else{
            //trocar imgem centro
            jogador.setDrawable(new SpriteDrawable(new Sprite(testuraJogador)));

        }
    }



    /**
     * verifica se as teclas estao pressionadas
     */
    private void capturaTeclas() {
        indoDireita = false;
        indoEsquerdo = false;
        atirando = false;



        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || clicouEsquerda()) {
            indoEsquerdo = true;

        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || clicouDireita()){
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.app.getType() == Application.ApplicationType.Android) {
            atirando = true;
        }
    }

    private boolean clicouDireita() {
        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();
            //captura clique/toque na tela do windows
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            //converte para uma cordenada no jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth / 2;

            if (posicao.x > meio) {
                return true;
            }
        }
            return false;
        }


    private boolean clicouEsquerda() {
        if (Gdx.input.isTouched()) {
            Vector3 posicao = new Vector3();
            //captura clique/toque na tela do windows
            posicao.x = Gdx.input.getX();
            posicao.y = Gdx.input.getY();
            //converte para uma cordenada no jogo
            posicao = camera.unproject(posicao);
            float meio = camera.viewportWidth / 2;

            if (posicao.x < meio) {
                return true;
            }
        }
        return false;
    }

    /**
     * e Chamado sempre que h� uma alteracao do tamanho da tela
     * @param width novo valor de largura da tela
     * @param height novo valor da altura da tela
     */
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    /**
     * e chamado sempre que o jogo for minimizado
     */
    @Override
    public void pause() {

    }

    /**
     * e chamado sempre que o jogo voltar para o primeiro plano
     */
    @Override
    public void resume() {

    }

    /**
     * e Chamando quando a tela for destruida
     */
    @Override
    public void dispose() {
        batch.dispose();
        palco.dispose();
        palcoInformacoes.dispose();
        fonte.dispose();
        testuraJogador.dispose();
        testuraJogadorDireito.dispose();
        testuraJogadorEsquerdo.dispose();
        testuraTiros.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
        for(Texture text : texturasExplosao){
            text.dispose();
            somTiro.dispose();
            somExplosao.dispose();
            somGameOver.dispose();
            musicaFundo.dispose();
        }
    }
}
