using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace INSAWorld
{
    /// <summary>
    /// 
    /// </summary>
    public class NewGameBuilder : IGameBuilder
    {
        /// <summary>
        /// 
        /// </summary>
        public MapStrategy MapStrategy { get; private set; }

        /// <summary>
        /// 
        /// </summary>
        private readonly List<string> playerNames;

        /// <summary>
        /// 
        /// </summary>
        public ReadOnlyCollection<string> PlayerNames
        {
            get
            {
                return playerNames.AsReadOnly();
            }
        }

        /// <summary>
        /// 
        /// </summary>
        private NewGameBuilder()
        {
            playerNames = new List<string>();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="array"></param>
        private void Shuffle(object[] array)
        {
            var random = new Random(DateTime.Now.Millisecond);
            var n = array.Length;
            while (n > 1)
            {
                var k = random.Next(n--);
                var tmp = array[n];
                array[n] = array[k];
                array[k] = tmp;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public static NewGameBuilder Create()
        {
            return new NewGameBuilder();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public Game Build()
        {
            var map = MapStrategy.BuildMap();

            Race[] races = { new Centaur(), new Cerberus(), new Cyclops() };
            Shuffle(races);

            var numberPlayers = playerNames.Count;
            var initialPositions = new PositionGenerator().GenerateRandomPositions(numberPlayers, map.Size);
            Shuffle(initialPositions);

            var players = new Player[numberPlayers];
            for (var i = 0; i < players.Length; i++)
                players[i] = new Player(playerNames[i], races[i], MapStrategy.NumberUnits, initialPositions[i]);
            Shuffle(players);

            return new Game(map, MapStrategy.MaximumNumberTurns, players);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="mapStrategy"></param>
        /// <returns></returns>
        public NewGameBuilder SetMap(MapStrategy mapStrategy)
        {
            MapStrategy = mapStrategy;

            return this;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public NewGameBuilder SetPlayerName(string name)
        {
            playerNames.Add(name);

            return this;
        }
    }
}