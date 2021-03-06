package de.croggle.ui.renderer.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.croggle.game.Color;
import de.croggle.game.board.AgedAlligator;
import de.croggle.game.board.Board;
import de.croggle.game.board.BoardObject;
import de.croggle.game.board.ColoredAlligator;
import de.croggle.game.board.Egg;
import de.croggle.game.board.InternalBoardObject;
import de.croggle.game.board.operations.CreateHeightMap;
import de.croggle.ui.renderer.objectactors.AgedAlligatorActor;
import de.croggle.ui.renderer.objectactors.BoardObjectActor;
import de.croggle.ui.renderer.objectactors.BoardObjectActorFactory;
import de.croggle.ui.renderer.objectactors.ColoredAlligatorActor;
import de.croggle.ui.renderer.objectactors.EggActor;

/**
 * A helper class providing functionality to fix {@link ActorLayout}s when the
 * represented {@link de.croggle.game.board.BoardObject} structure has changed.
 * For example, if an {@link de.croggle.game.board.AgedAlligator} vanishes, its
 * children need to be sifted up
 * 
 * Note: does not have an explicit access modifier since only the
 * {@link ActorLayout} class itself is supposed to make use of the functions
 * inside this class
 */
class ActorLayoutFixer extends ActorLayouter {

	private final List<ActorDelta> deltas;
	private ActorDelta unused;

	private final ActorLayout l;

	private final AgedAlligatorActor aaaDummy;
	private final ColoredAlligatorActor caaDummy;
	private final EggActor eaDummy;

	private InternalBoardObject lastProvidedFor;

	private ActorLayoutFixer(ActorLayout l, Board b) {
		super(b, l.getLayoutConfiguration());
		this.l = l;
		aaaDummy = BoardObjectActorFactory
				.instantiateAgedAlligatorActor(new AgedAlligator(true, true));
		caaDummy = BoardObjectActorFactory.instantiateColoredAlligatorActor(
				new ColoredAlligator(true, true, Color.uncolored(), true),
				false);
		eaDummy = BoardObjectActorFactory.instantiateEggActor(new Egg(true,
				true, Color.uncolored(), true), false);

		deltas = new ArrayList<ActorDelta>(l.size());
	}

	/**
	 * Calculates and returns all property-deltas that would need to be applied
	 * to a layout to match the current state of the given board, leaving all
	 * children untouched (i.e. moving them up into the next higher parent)
	 * 
	 * @param l
	 * @param b
	 * @return
	 */
	public static List<ActorDelta> getDeltas(ActorLayout l, Board b) {
		ActorLayoutFixer fixer = new ActorLayoutFixer(l, b);
		fixer.doLayout();
		l.getLayoutStatistics().setWidthMap(fixer.widthMap);
		// TODO as this is not really necessary for the fix process, maybe
		// implement it more efficiently as a byproduct?
		ActorLayoutConfiguration config = l.getLayoutConfiguration();
		Map<BoardObject, Float> heightMap = CreateHeightMap.create(b,
				config.getUniformObjectHeight(),
				config.getVerticalScaleFactor(), config.getVerticalPadding());
		l.getLayoutStatistics().setHeightMap(heightMap);
		return fixer.deltas;
	}

	@Override
	protected AgedAlligatorActor provideAgedAlligatorActor(
			AgedAlligator alligator) {
		lastProvidedFor = alligator;
		return aaaDummy;
	}

	@Override
	protected ColoredAlligatorActor provideColoredAlligatorActor(
			ColoredAlligator alligator) {
		lastProvidedFor = alligator;
		return caaDummy;
	}

	@Override
	protected EggActor provideEggActor(Egg egg) {
		lastProvidedFor = egg;
		return eaDummy;
	}

	@Override
	protected void notifyLayouted(BoardObjectActor actor) {
		BoardObjectActor current = l.getActor(lastProvidedFor);
		ActorDelta delta = buildDelta(current, actor);
		if (delta != null) {
			deltas.add(delta);
		}
	}

	private ActorDelta buildDelta(BoardObjectActor current,
			BoardObjectActor newActor) {
		if (unused == null) {
			unused = l.getDeltaPool().obtain();
			unused.reset();
		}
		if (current == null) {
			unused.setCreated(true);
			BoardObjectActor actor = BoardObjectActorFactory.createActorFor(
					lastProvidedFor, l.getLayoutConfiguration()
							.isColorBlindEnabled());
			actor.setX(newActor.getX());
			actor.setY(newActor.getY());
			actor.setWidth(newActor.getWidth());
			actor.setHeight(newActor.getHeight());
			unused.setActor(actor);
		} else {
			unused.setActor(current);
			if (current.getX() != newActor.getX()) {
				unused.setxChanged(true);
				unused.setNewX(newActor.getX());
			}
			if (current.getY() != newActor.getY()) {
				unused.setyChanged(true);
				unused.setNewY(newActor.getY());
			}
			if (current.getWidth() != newActor.getWidth()) {
				unused.setWidthChanged(true);
				unused.setNewWidth(newActor.getWidth());
			}
			if (current.getHeight() != newActor.getHeight()) {
				unused.setHeightChanged(true);
				unused.setNewHeight(newActor.getHeight());
			}
		}

		if (unused.anythingChanged()) {
			ActorDelta result = unused;
			unused = null;
			return result;
		} else {
			return null;
		}
	}
}
