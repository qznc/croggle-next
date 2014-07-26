package de.croggle.ui.screens;

import static de.croggle.data.LocalizationHelper._;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.croggle.Croggle;
import de.croggle.ui.ConfirmInterface;
import de.croggle.ui.actors.YesNoDialog;

public class QuitGameOverlay implements Screen {
	private ShapeRenderer shapes;
	private final Color shade;
	private Screen screenBelow;
	private final Stage stage;
	private final Table table;
	private final Croggle game;
	private final InputMultiplexer inputMediator;
	private final Viewport vp;
	private Dialog quitDialog = null;

	public QuitGameOverlay(Croggle game) {
		this.game = game;
		this.screenBelow = null;

		vp = new com.badlogic.gdx.utils.viewport.ScalingViewport(
				Scaling.stretchX, 1024, 600);
		stage = new Stage(vp);
		inputMediator = new InputMultiplexer(stage, new BackButtonHandler());

		shade = new Color(0, 0, 0, .5f);
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
	}

	public void setOverlayedScreen(Screen s) {
		this.screenBelow = s;
	}

	@Override
	public void render(float delta) {
		if (screenBelow != null) {
			screenBelow.render(delta);
		}

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapes.setProjectionMatrix(vp.getCamera().combined);
		shapes.begin(ShapeType.Filled);
		shapes.setColor(shade);
		shapes.rect(0, 0, vp.getWorldWidth(), vp.getWorldHeight());
		shapes.end();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		vp.update(width, height);
	}

	@Override
	public void show() {
		/*
		 * We need to lazy-load the dialog, because its style gets loaded
		 * asynchronously. Since the QuitGameOverlay is created on startup
		 * during the loading screen, an AssetManager.finishLoading would
		 * destroy the desired loading progress effect.
		 */
		if (quitDialog == null) {
			quitDialog = new YesNoDialog(_("quit_game_prompt"),
					new ConfirmInterface() {
						@Override
						public void yes() {
							// do whatever needs to be disposed on exit, exit()
							// only
							// closes the activity
							Gdx.app.exit();
						}

						@Override
						public void no() {
							hide();
							if (screenBelow != null) {
								QuitGameOverlay.this.game
										.setScreen(screenBelow);
							} else {
								throw new IllegalStateException(
										"Don't know where to return to if no screen is set as OverlayedScreen");
							}
						}
					});
		}
		// call 'show' every time, since it is hidden/removed after a selection
		quitDialog.show(stage);

		shapes = new ShapeRenderer();
		Gdx.input.setInputProcessor(inputMediator);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		stage.dispose();
		shapes.dispose();
	}

	private class BackButtonHandler extends InputAdapter {
		@Override
		public boolean keyUp(int keycode) {
			if (keycode == Keys.BACK) {
				if (screenBelow != null) {
					game.setScreen(screenBelow);
					return true;
				} else {
					throw new IllegalStateException(
							"Don't know where to return to if no screen is set as OverlayedScreen");
				}
			}
			return false;
		}
	}
}
