using GalaSoft.MvvmLight;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;

namespace INSAWorld
{
    /// <summary>
    /// 
    /// </summary>
    [Serializable]
    public class Game
    {
        /// <summary>
        /// 
        /// </summary>
        public Map Map { get; }

        /// <summary>
        /// 
        /// </summary>
        public int MaximumNumberTurns { get; }

        /// <summary>
        /// 
        /// </summary>
        public ReadOnlyCollection<Player> Players { get; }

        /// <summary>
        /// 
        /// </summary>
        private readonly List<Action> history;

        /// <summary>
        /// 
        /// </summary>
        public ReadOnlyCollection<Action> History
        {
            get
            {
                return history.AsReadOnly();
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public int CurrentTurn { get; private set; }

        /// <summary>
        /// 
        /// </summary>
        private int currentPlayer;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="map"></param>
        /// <param name="maximumNumberTurns"></param>
        /// <param name="players"></param>
        public Game(Map map, int maximumNumberTurns, params Player[] players)
        {
            if (map == null)
                throw new ArgumentNullException("map");
            if (maximumNumberTurns < 1)
                throw new ArgumentOutOfRangeException("Maximum number of turns was out of range. Must be higher than 0.", "maximumNumberTurns");
            if (players.Length < 2)
                throw new ArgumentOutOfRangeException("Number of players was out of range. Must be at least 2.", "players");
            // Checks that no player is null
            for (var i = 0; i < players.Length; i++)
                if (players[i] == null)
                    throw new ArgumentNullException("All players must be not null.", "players[" + i + "]");
            // Checks that all players have a different race
            for (var i = 0; i < players.Length; i++)
                for (var j = 0; j < players.Length; j++)
                    if (players[i].Race.GetType() == players[j].Race.GetType() && i != j)
                        throw new ArgumentException("Two players must not have the same race.", "players[" + i + "] and players[" + j + "]");

            Map = map;
            MaximumNumberTurns = maximumNumberTurns;
            Players = new ReadOnlyCollection<Player>(players);
            history = new List<Action>();
            CurrentTurn = 1;
            currentPlayer = 0;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        private bool NextTurn()
        {
            if (++CurrentTurn > MaximumNumberTurns)
                return false;

            foreach (var player in Players)
            {
                player.ComputeTotalVictoryPoints(Map);
                foreach (var unit in player.Units)
                {
                    unit.RecoverHealth();
                    unit.ResetMove();
                }
            }

            return true;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public bool NextPlayer()
        {
            currentPlayer = (currentPlayer + 1) % Players.Count;

            if (IsAPlayerIsDecimated() || currentPlayer == 0 && !NextTurn())
                return false;

            return true;
        }

        public bool IsAPlayerIsDecimated()
        {
            foreach (var player in Players)
                if (player.IsDecimated())
                    return true;

            return false;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="action"></param>
        public void DoAction(Action action)
        {
            history.Add(action);
            action.Do();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public Player GetCurrentPlayer()
        {
            return Players[currentPlayer];
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public Player GetWinner()
        {
            var winner = Players[0];
            for (var i = 1; i < Players.Count; i++)
                if (winner.VictoryPoints < Players[i].VictoryPoints)
                    winner = Players[i];

            return winner;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="path"></param>
        public void SaveGame(string path)
        {
            var stream = new FileStream(path, FileMode.Create, FileAccess.Write, FileShare.None);
            new BinaryFormatter().Serialize(stream, this);
            stream.Close();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        static public Game LoadGame(string path)
        {
            var stream = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.Read);
            var game = (Game) new BinaryFormatter().Deserialize(stream);
            stream.Close();

            return game;
        }

        /// <summary>
        /// 
        /// </summary>
        public void RewindGame()
        {

            foreach (var action in (History))
            {
                try
                {
                    action.Undo();
                }
                catch (Exception e)
                {
                    
                    throw;
                }
                
            }
        }
    }
}