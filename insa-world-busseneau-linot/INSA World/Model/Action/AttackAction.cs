using System;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of a player's attack action.
    /// </summary>
    [Serializable]
    public class AttackAction : Action
    {
        /// <summary>
        /// The attacking player's unit.
        /// </summary>
        public Unit Attacker { get; }

        /// <summary>
        /// The initial position of the attacking player's unit.
        /// </summary>
        public Position InitialAttackerPosition { get; }

        /// <summary>
        /// The defending player's unit.
        /// </summary>
        public Unit Defender { get; }

        /// <summary>
        /// The losing unit of the attack action.
        /// </summary>
        public Unit Loser { get; }

        /// <summary>
        /// The cost of health points.
        /// </summary>
        public int HealthPointsLost { get; }

        /// <summary>
        /// Constructs an attack action. This constructor needs the map to compute the cost of the move action.
        /// </summary>
        /// <param name="map">The game map.</param>
        /// <param name="attacker">The attacking player's unit.</param>
        /// <param name="defender">The defending player's unit.</param>
        /// <exception cref="ArgumentNullException">If map is null.</exception>
        /// <exception cref="ArgumentNullException">If attacker is null.</exception>
        /// <exception cref="ArgumentNullException">If defender is null.</exception>
        public AttackAction(Map map, Unit attacker, Unit defender) : base(attacker.CostAction(map, defender.Position))
        {
            Attacker = attacker;
            InitialAttackerPosition = Attacker.Position;
            Defender = defender;

            var random = new Random(DateTime.Now.Millisecond);
            var realAttackPoints = attacker.GetRealAttackPoints();
            var realDefensePoints = defender.GetRealDefensePoints();
            var ratio = realAttackPoints / (realAttackPoints + realDefensePoints);
            double d = random.NextDouble();
            if (ratio >= d)
            {
                Loser = defender;
                HealthPointsLost = random.Next(1, (int) (realAttackPoints + 1));
            }
            else
            {
                Loser = attacker;
                HealthPointsLost = random.Next(1, (int) (realDefensePoints + 1));
            }
            HealthPointsLost = Loser.HealthPoints/random.Next(1,3);
            HealthPointsLost %= (Loser.HealthPoints+1);

        }

        /// <summary>
        /// Attacks the defending player's unit with the attacking player's unit.
        /// </summary>
        public override void Do()
        {
            Loser.HealthPoints -= HealthPointsLost;
            Attacker.MovePoints -= (MovePointsLost)%Unit.MAX_MOVE_POINTS;
            if (Loser == Defender && Defender.IsDead())
                Attacker.Position = Defender.Position;
        }

        /// <summary>
        /// Resets the battle.
        /// </summary>
        public override void Undo()
        {
            if (Defender.IsDead())
                Attacker.Position = InitialAttackerPosition;
            Attacker.MovePoints = (MovePointsLost + Attacker.MovePoints)%Unit.MAX_MOVE_POINTS;
            Loser.HealthPoints = (HealthPointsLost+Loser.HealthPoints)%Loser.InitialHealthPoints;
        }
    }
}