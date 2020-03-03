package com.ivanlg.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class flappybird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture[] passaro;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoAlto;
    private Texture gameover;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passarocirculo;
    private Rectangle canoToporeta;
    private Rectangle canobaixoreta;
    //private ShapeRenderer shape;


    private Random numerorandomico;

    private float largura;
    private float altura;
    private int pontuacao;
    private int estado=0;
    private int flag=0;
    private float velocidadequeda=0;
    private float posinivet;
    private float variacao=0;
    private float posicaomovicamohori;
    private float espacocanos=400;
    private float deltatime;
    private float alturacanorandom=0;

    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_LARGUTA =768;
    private final float VIRTUAL_ALTURA = 1024;

    @Override
    public void create () {
        batch = new SpriteBatch();
        fundo = new Texture("fundo.png");
        passaro = new Texture[3];
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");
        canoAlto = new Texture("cano_topo_maior.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        gameover = new Texture("game_over.png");

        largura = VIRTUAL_LARGUTA;
        altura = VIRTUAL_ALTURA;
        posinivet = altura/2;
        posicaomovicamohori = largura;
        numerorandomico = new Random();
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);
        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);
        passarocirculo = new Circle();
        //shape = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_LARGUTA/2,VIRTUAL_ALTURA/2,0);
        viewport = new FitViewport(VIRTUAL_LARGUTA,VIRTUAL_ALTURA,camera);
    }

    @Override
    public void render () {
        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

        deltatime=Gdx.graphics.getDeltaTime();

        if (estado==0){
            variacao+=deltatime*5;
            if (variacao>2) variacao=0;
            if (Gdx.input.justTouched()){
                estado=1;
            }
        }else if (estado==2) {
            velocidadequeda++;
            if (posinivet > 0 || velocidadequeda < 0) {
                posinivet -= velocidadequeda;
            }



            if (Gdx.input.justTouched()){
                estado=0;
                pontuacao=0;
                velocidadequeda=0;
                posinivet=altura/2;
                posicaomovicamohori=largura;
            }

        }else {
            variacao+=deltatime*5;
            if (variacao>2) variacao=0;
            posicaomovicamohori -= deltatime * 300;
            velocidadequeda++;

            if (posinivet > 0 || velocidadequeda < 0) {
                posinivet -= velocidadequeda;
            }

            if (Gdx.input.justTouched()) {
                velocidadequeda = -15;
            }

            if (posicaomovicamohori < -canoBaixo.getWidth()) {
                posicaomovicamohori = largura;
                alturacanorandom = numerorandomico.nextInt(400) - 200;
                flag=0;
            }
            if (posicaomovicamohori<120 && flag==0){
                pontuacao++;
                flag=1;
            }
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(fundo,0,0,largura,altura);
        batch.draw(canoAlto,posicaomovicamohori,altura/2+espacocanos/2+alturacanorandom);
        batch.draw(canoBaixo,posicaomovicamohori,altura/2 -canoBaixo.getHeight() -espacocanos/2+alturacanorandom);
        batch.draw(passaro[(int)variacao],120,posinivet);
        fonte.draw(batch,String.valueOf(pontuacao),largura/2-5,altura-50);
        if (estado==2){
            batch.draw(gameover,largura/2-gameover.getWidth()/2,altura/2);
            mensagem.draw(batch,"Toque para Reiniciar!",largura/2-200,altura/2-gameover.getHeight()/2);
        }
        batch.end();

        passarocirculo.set(120+passaro[0].getWidth()/2,posinivet+passaro[0].getHeight()/2,passaro[0].getWidth()/2);
        canoToporeta = new Rectangle(posicaomovicamohori,altura/2+espacocanos/2+alturacanorandom,canoAlto.getWidth(),canoAlto.getHeight());
        canobaixoreta = new Rectangle(posicaomovicamohori,altura/2 -canoBaixo.getHeight() -espacocanos/2+alturacanorandom,canoBaixo.getWidth(),canoBaixo.getHeight());

        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passarocirculo.x,passarocirculo.y,passarocirculo.radius);
        shape.rect(canobaixoreta.x,canobaixoreta.y,canobaixoreta.width,canobaixoreta.height);
        shape.rect(canoToporeta.x,canoToporeta.y,canoToporeta.width,canoToporeta.height);
        shape.setColor(Color.RED);
        shape.end();*/

        if (Intersector.overlaps(passarocirculo,canobaixoreta)|| Intersector.overlaps(passarocirculo,canoToporeta)||posinivet<=0||posinivet>=altura){
            estado=2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
