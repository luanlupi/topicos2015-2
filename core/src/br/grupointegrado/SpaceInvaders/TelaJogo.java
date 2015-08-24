package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;



/**
 * Created by Luan on 03/08/2015.
 */
public class TelaJogo extends TelaBase {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Stage palco;
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

        initTesturas();
        initfonte();
        initInformacoes();
        initJogador();

    }

    private void initTesturas() {
        testuraTiros = new Texture("sprites/shot.png");
        texturaMeteoro1 = new Texture("sprites/enemie-1.png");
        texturaMeteoro2 = new Texture("sprites/enemie-2.png");
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
        palco.addActor(lbGameOver);
    }

    /**
     * Instancia os objetos de fontes
     */

    private void initfonte() {
        fonte = new BitmapFont();
    }

    /**
     * chamado a todo quadro de atualizacao do jogo(fps)
     * @param delta tempo entre um quadro e outro(em segundo)
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.15f, .15f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        lbPontuacao.setPosition(10, camera.viewportHeight - 20);
        lbPontuacao.setText(pontuacao + "pontos" );

        lbGameOver.setPosition(camera.viewportWidth / 2 - lbGameOver.getWidth() / 2, camera.viewportHeight / 2);
        lbGameOver.setVisible(gameOver == true);

        if(gameOver == false) {

            capturaTeclas();
            atualizarJogador(delta);
            atualizarTiros(delta);
            atualizarMateoros(delta);
            detectarColisoes(meteoros1, 5);
            detectarColisoes(meteoros2, 15);
        }
        //atualiza a situacao do palco

        palco.act(delta);
        //desenha o palco na tela
        palco.draw();
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
                }
            }
            //detecta colisao com o player
            if(recJogador.overlaps(recMeteoro)){
                //ocoorre colisao do jogador com meteoro 1
                gameOver = true;
            }

        }

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

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            indoEsquerdo = true;

        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            indoDireita = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            atirando = true;
        }
    }

    /**
     * e Chamado sempre que há uma alteracao do tamanho da tela
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
        fonte.dispose();
        testuraJogador.dispose();
        testuraJogadorDireito.dispose();
        testuraJogadorEsquerdo.dispose();
        testuraTiros.dispose();
        texturaMeteoro1.dispose();
        texturaMeteoro2.dispose();
    }
}
