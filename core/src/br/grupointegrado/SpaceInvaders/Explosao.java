package br.grupointegrado.SpaceInvaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Luan on 31/08/2015.
 */
public class Explosao {

    private static float tempo_troca = 0.5f / 17f; //aprox. 0,05 seg.

    private int estagio = 0; //controla o estagio de 0 a 16
    private Array<Texture> testuras;
    private Image ator;
    private float tempoAcumulado = 0;

    public Explosao(Image ator, Array<Texture> testuras){
        this.ator = ator;
        this.testuras = testuras;
    }
    public Image getAtor(){
        return ator;
    }



    /**
     * calcula o tempo acumulado e realiza a troca
     * do estagio da explosao
     * Exmplo:
     * cada quadro demora 0,016 seg.
     *cada imagem deve permanencer 0,05 seg.
     * @param delta
     */

    public void atulizar(float delta){
        tempoAcumulado = tempoAcumulado + delta;
        if (tempoAcumulado >= tempo_troca){
            tempoAcumulado = 0;
            estagio ++;
            Texture texture = testuras.get(estagio);
            ator.setDrawable(new SpriteDrawable(new Sprite(texture)));

        }
    }

    public  int getEstagio(){
        return estagio;
    }
}
