using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class GameTests
    {
        [TestMethod]
        public void GameTest()
        {
            var game = NewGameBuilder.Create()
                .SetMap(new DemoMap())
                .SetPlayerName("Player 1")
                .SetPlayerName("Player 2")
                .Build();
            Assert.AreEqual(game.Map.Size, 6);
        }

        [TestMethod]
        public void NextPlayerTest()
        {
            var game = NewGameBuilder.Create()
                .SetMap(new DemoMap())
                .SetPlayerName("Player 1")
                .SetPlayerName("Player 2")
                .Build();
            var p1 = game.Players[0];
            var p2 = game.Players[1];
            Assert.AreSame(p1, game.GetCurrentPlayer());
            game.NextPlayer();
            Assert.AreSame(p2, game.GetCurrentPlayer());
        }

        [TestMethod]
        public void SaveLoadGameTest()
        {
            var game = NewGameBuilder.Create()
                .SetMap(new DemoMap())
                .SetPlayerName("Player 1")
                .SetPlayerName("Player 2")
                .Build();

            for (var i = 0; i < game.Players.Count; i++)
            {
                game.Players[0].Units[i].Position = new Position(0, 0);
                game.Players[1].Units[i].Position = new Position(5, 5);
            }

            game.DoAction(new MoveAction(game.Map, game.GetCurrentPlayer().Units[0], new Position(1, 1)));
            game.NextPlayer();
            game.DoAction(new MoveAction(game.Map, game.GetCurrentPlayer().Units[0], new Position(5, 5)));
            game.NextPlayer();

            var path = "tmp.bin";
            game.SaveGame(path);
            var loadedGame = Game.LoadGame(path);

            for (var i = 0; i < game.Players.Count; i++)
            {
                Assert.AreEqual(game.Players[i].Name, loadedGame.Players[i].Name);
                Assert.AreEqual(game.Players[i].VictoryPoints, loadedGame.Players[i].VictoryPoints);
                for (var j = 0; j < game.Players[i].Units.Count; j++)
                    Assert.AreEqual(game.Players[i].Units[j].Position, loadedGame.Players[i].Units[j].Position);
            }
            Assert.AreEqual(game.MaximumNumberTurns, loadedGame.MaximumNumberTurns);
            Assert.AreEqual(game.CurrentTurn, loadedGame.CurrentTurn);
        }
    }
}