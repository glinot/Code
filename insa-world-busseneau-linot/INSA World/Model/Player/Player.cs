using GalaSoft.MvvmLight;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace INSAWorld
{
    /// <summary>
    /// Implementation of a player of the game.
    /// </summary>
    [Serializable]
    public class Player
    {
        /// <summary>
        /// The player's name.
        /// </summary>
        public string Name { get; }

        /// <summary>
        /// The race which the player plays during the game.
        /// The race is randomly chosen.
        /// </summary>
        public Race Race { get; }

        private readonly List<Unit> units;

        /// <summary>
        /// The player's units.
        /// The units are automaticaly created and cannot be deleted.
        /// </summary>
        public ReadOnlyCollection<Unit> Units
        {
            get
            {
                return units.AsReadOnly();
            }
        }

        private int victoryPoints;

        /// <summary>
        /// The number of victory points.
        /// </summary>
        /// <exception cref="ArgumentOutOfRangeException">If the victory points to add is negative.</exception>
        public int VictoryPoints
        {
            get
            {
                return victoryPoints;
            }

            private set
            {
                if (value < 0)
                    throw new ArgumentOutOfRangeException("Victory points was out of range. Must be non-negative.", "value");

                victoryPoints = value;
            }
        }

        /// <summary>
        /// Constructs a player and creates the warriors.
        /// Makes sure that the race and the initial position has been chosen randomly.
        /// </summary>
        /// <param name="name">The player's name.</param>
        /// <param name="race">The player's race.</param>
        /// <param name="numberUnits">The number of units to created.</param>
        /// <param name="initialPosition">The initial position of the player's units.</param>
        /// <exception cref="ArgumentNullException">If the name is null or empty.</exception>
        /// <exception cref="ArgumentNullException">If the race is null.</exception> 
        /// <exception cref="ArgumentOutOfRangeException">If the numbers units is lower than 1.</exception>
        /// <exception cref="ArgumentNullException">If the initial position is null.</exception>
        public Player(string name, Race race, int numberUnits, Position initialPosition)
        {
            if (string.IsNullOrWhiteSpace(name))
                throw new ArgumentNullException("Name is invalid. Must be not null or not empty.", "name");
            if (race == null)
                throw new ArgumentNullException("race");
            if (numberUnits < 1)
                throw new ArgumentOutOfRangeException("Number of units was out of range. Must be higher than 0.", "numberUnits");
            if (initialPosition == null)
                throw new ArgumentNullException("initialPosition");

            Name = name;
            Race = race;
            units = new List<Unit>();
            for (var i = 0; i < numberUnits; i++)
                units.Add(Race.ProduceWarrior(initialPosition));
            VictoryPoints = 0;
        }

        /// <summary>
        /// Computes the victory points of the current turn.
        /// Each player's unit is scanned to compute the victory points.
        /// </summary>
        /// <param name="map">The map where the player plays.</param>
        /// <exception cref="ArgumentNullException">If the map is null.</exception>
        public void ComputeTotalVictoryPoints(Map map)
        {
            if (map == null)
                throw new ArgumentNullException("map");

            for (var i = 0; i < units.Count; i++)
            {
                var j = i + 1;
                while (j != -1 && j < units.Count)
                    if (units[i].Position == units[j].Position)
                        j = -1;
                    else
                        j++;

                if (j != -1)
                    victoryPoints += Race.ComputeVictoryPoints(map, units[i]);
            }                
        }

        /// <summary>
        /// Checks if the player has all his units dead.
        /// </summary>
        /// <returns>True if all the player's units are dead, false otherwise.</returns>
        public bool IsDecimated()
        {
            foreach (var unit in Units)
                if (!unit.IsDead())
                    return false;
            return true;
        }
    }
}