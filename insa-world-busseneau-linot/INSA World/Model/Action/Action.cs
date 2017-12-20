using System;

namespace INSAWorld
{
    /// <summary>
    /// Abstract implementation of a player's action.
    /// </summary>
    [Serializable]
    public abstract class Action
    {
        /// <summary>
        /// The cost of move points. 
        /// </summary>
        public double MovePointsLost { get; }

        /// <summary>
        /// Constructs a player's action with the cost of move points specified.
        /// </summary>
        /// <param name="movePointsLost">Cost of move points.</param>
        /// <exception cref="ArgumentOutOfRangeException">If movePointsLost is negative or lower than or equals to <see cref="Unit.MAX_MOVE_POINTS" /></exception>
        protected Action(double movePointsLost)
        {
            if (movePointsLost < 0 && movePointsLost > Unit.MAX_MOVE_POINTS)
                throw new ArgumentOutOfRangeException("Move points lost was out of range. Must be non-negative and lower than or equals to the maximum move points (" + Unit.MAX_MOVE_POINTS + ").", "movePointsLost");

            MovePointsLost = movePointsLost;
        }

        /// <summary>
        /// Does the player's action in the game.
        /// </summary>
        public abstract void Do();

        /// <summary>
        /// Undoes the player's action in the game.
        /// </summary>
        public abstract void Undo();
    }
}