using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;

namespace INSAWorld
{
    public class MoveManager
    {
        public Game g { get; set; }

        public MoveManager(Game g)
        {
            if (g == null)
                throw new ArgumentNullException("Game can't be null");
            this.g = g;
        }

        /// <summary>
        /// Get discrete distance to the closest enemy 
        /// </summary>
        /// <param name="p"></param>
        /// <param name="u"></param>
        /// <returns></returns>
        private int GetDistanceToEnemy(Player p, Unit u)
        {
            var res = -1;
            foreach (var player in g.Players)
            {
                if (player != p)
                {
                    foreach (var unit in p.Units)
                    {
                        var tempDistance = Math.Abs(u.Position.X - unit.Position.X) +
                                           Math.Abs(u.Position.Y - unit.Position.Y);
                        if (tempDistance > res)
                            res = tempDistance;
                    }
                }
            }
            return res;
        }
        /// <summary>
        /// Get Reachable Moves From a Player and Unit knowing the remaining MovePoints
        /// </summary>
        /// <param name="p"></param>
        /// <param name="u"></param>
        /// <returns></returns>

        private IList<Move> GetReachableMove(Player p, Unit u)
        {
            var l = new List<Move>();
            var positions = u.FindReachablePositions(g.Map);
            foreach (var pos in positions)
            {
                var cost = u.CostAction(g.Map, pos);
                var distance = GetDistanceToEnemy(p, u);

                var m = new Move();
                m.x = pos.X;
                m.y = pos.Y;
                m.distanceToEnemy = distance;
                m.movePoints = cost;
                m.bonus = p.Race.ComputeVictoryPoints(g.Map, u);
                l.Add(m);
            }

            return l;

        }


        /// <summary>
        /// Compute the 3 best Moves for given Player and Unit 
        /// </summary>
        /// <param name="p"></param>
        /// <param name="u"></param>
        /// <returns></returns>

        public IList<Position> GetBestMove(Player p , Unit u )
        {
            var res=  new List<Position>();
            var reachableMoves = GetReachableMove(p, u).ToArray();
            var nbResults = Math.Min(3, reachableMoves.Length);
            var bestMoves = new Move[nbResults];

            var pinnedReachableMoves = GCHandle.Alloc(reachableMoves, GCHandleType.Pinned);
            var pinnedBestMoves = GCHandle.Alloc(bestMoves, GCHandleType.Pinned);

            MoveManager_getBestMoves(
                reachableMoves.Length,
                bestMoves.Length,
                pinnedReachableMoves.AddrOfPinnedObject(),
                pinnedBestMoves.AddrOfPinnedObject());

            pinnedBestMoves.Free();
            pinnedReachableMoves.Free();
            foreach (var move in bestMoves)
                res.Add(new Position(move.x , move.y));
            
            return res;
        }

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        static extern void MoveManager_getBestMoves(int nIn, int nOut, IntPtr moves, IntPtr res);

        [StructLayout(LayoutKind.Sequential)]
        private struct Move
        {
            public int x { get; set; }
            public int y { get; set; }
            public int distanceToEnemy { get; set; }
            public int bonus { get; set; }
            public double movePoints { get; set; }
        }
    }
}
