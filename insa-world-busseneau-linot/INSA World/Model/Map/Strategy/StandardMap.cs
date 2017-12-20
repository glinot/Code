namespace INSAWorld
{
    /// <summary>
    /// Standard map strategy.
    /// </summary>
    public class StandardMap : MapStrategy
    {
        public StandardMap() : base(14, 30, 8) {}
        public override string ToString()
        {
            return "Standard";
        }
    }
}