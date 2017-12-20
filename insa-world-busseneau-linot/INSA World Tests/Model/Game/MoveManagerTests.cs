using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace INSAWorld.Tests
{
    [TestClass]
    public class MoveManagerTests
    {
        [TestMethod]
        public void StructTest()
        {
            var game = NewGameBuilder.Create()
                .SetMap(new DemoMap())
                .SetPlayerName("PLayer 1")
                .SetPlayerName("Player 2")
                .Build();
            var moveManager = new MoveManager(game);
            var i = moveManager.GetBestMove(game.GetCurrentPlayer(), game.GetCurrentPlayer().Units[0]);
        }
    }
}