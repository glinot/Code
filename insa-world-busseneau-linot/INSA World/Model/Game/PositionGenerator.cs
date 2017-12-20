using System;
using System.Runtime.InteropServices;

namespace INSAWorld
{
    /// <summary>
    /// 
    /// </summary>
    public class PositionGenerator : IDisposable
    {
        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static IntPtr PositionGenerator_new();

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static IntPtr PositionGenerator_delete(IntPtr positionGenerator);

        [DllImport("INSA World Library.dll", CallingConvention = CallingConvention.Cdecl)]
        private extern static void PositionGenerator_generateRandomPositions(IntPtr positionGenerator, int[] positions, int numberPlayers, int mapSize);

        /// <summary>
        /// 
        /// </summary>
        private bool disposed;

        /// <summary>
        /// 
        /// </summary>
        private readonly IntPtr nativePositionGenerator;
        
        /// <summary>
        /// 
        /// </summary>
        public PositionGenerator()
        {
            disposed = false;
            nativePositionGenerator = PositionGenerator_new();
        }

        /// <summary>
        /// 
        /// </summary>
        ~PositionGenerator()
        {
            Dispose(false);
            PositionGenerator_delete(nativePositionGenerator);
        }

        /// <summary>
        /// 
        /// </summary>
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="disposing"></param>
        protected virtual void Dispose(bool disposing)
        {
            if (disposed)
                return;
            if (disposing)
                PositionGenerator_delete(nativePositionGenerator);
            disposed = true;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="numberPlayers"></param>
        /// <param name="mapSize"></param>
        /// <returns></returns>
        public Position[] GenerateRandomPositions(int numberPlayers, int mapSize)
        {
            var positionsWrapper = new int[numberPlayers * 2];
            PositionGenerator_generateRandomPositions(nativePositionGenerator, positionsWrapper, numberPlayers, mapSize);

            var positions = new Position[numberPlayers];
            for (var i = 0; i < numberPlayers; i++)
                positions[i] = new Position(positionsWrapper[2 * i], positionsWrapper[2 * i + 1]);
            
            return positions;
        }
    }
}