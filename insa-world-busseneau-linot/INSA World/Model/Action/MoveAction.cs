using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of a player's move action.
    /// </summary>
    [Serializable]
    public class MoveAction : Action
    {
        /// <summary>
        /// The player's unit to move.
        /// </summary>
        public Unit Unit { get; }

        /// <summary>
        /// The new player's unit position.
        /// </summary>
        public Position Position { get; private set; }

        /// <summary>
        /// Constructs a move action. This constructor needs the map to compute the cost of the move action.
        /// </summary>
        /// <param name="map">The game map.</param>
        /// <param name="unit">The player's unit to move.</param>
        /// <param name="position">The new player's unit position.</param>
        /// <exception cref="ArgumentNullException">If map is null.</exception>
        /// <exception cref="ArgumentNullException">If unit is null.</exception>
        /// <exception cref="ArgumentNullException">If position is null.</exception>
        public MoveAction(Map map, Unit unit, Position position) : base(unit.CostAction(map, position))
        {
            if (unit == null)
                throw new ArgumentNullException("unit");

            Unit = unit;
            Position = position;
        }

        /// <summary>
        /// Moves the player's unit in the game to the new position.
        /// </summary>
        public override void Do()
        {
            Unit.MovePoints -= MovePointsLost;
            var oldPosition = Unit.Position;
            Unit.Position = Position;
            Position = oldPosition;
        }

        /// <summary>
        /// Moves the player's unit in the game to the old position.
        /// </summary>
        public override void Undo()
        {
            
            Unit.MovePoints =(Unit.MovePoints + MovePointsLost)%Unit.MAX_MOVE_POINTS ;
            var newPosition = Unit.Position;
            Unit.Position = Position;
            Position = newPosition;
        }
    }
}