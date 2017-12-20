using System;

namespace INSAWorld
{
    /// <summary>
    /// A cyclops warrior.
    /// This object is only created by the race.
    /// </summary>
    [Serializable]
    public class CyclopsWarrior : Unit
    {
        /// <summary>
        /// Constructs a cyclops warrior.
        /// </summary>
        /// <param name="initialPosition">The initial unit position.</param>
        public CyclopsWarrior(Position initialPosition) : base(12, 4, 6, initialPosition) {}
    }
}
