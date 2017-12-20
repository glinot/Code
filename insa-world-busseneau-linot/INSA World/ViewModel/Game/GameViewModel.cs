using GalaSoft.MvvmLight;
using GalaSoft.MvvmLight.Command;
using GalaSoft.MvvmLight.Messaging;
using MaterialDesignThemes.Wpf;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Runtime.ExceptionServices;
using System.Threading;
using System.Windows;
using System.Windows.Threading;

namespace INSAWorld.ViewModel
{
    public class GameViewModel : ViewModelBase
    {
        public Game Game { get; private set; }

        private bool isReplay;
        public bool IsEnabled
        {
            get
            {
                return !isReplay;
            }
        }

        public PlayerViewModel Player1 { get; private set; }

        public PlayerViewModel Player2 { get; private set; }

        public IList<TileViewModel> Tiles { get; private set; }

        public UnitViewModel SelectedUnit { get; private set; }

        public RelayCommand<TileViewModel> ActionCommand { get; }

        public RelayCommand NextTurnCommand { get; }

        public RelayCommand SaveGameDialogCommand { get; }

        public RelayCommand LoadGameDialogCommand { get; }

        public RelayCommand ExitGameDialogCommand { get; }

        public GameViewModel()
        {
            Messenger.Default.Register<GameMessage>(this, message =>
            {
                Game = message.Game;
                isReplay = message.IsReplay;
                RaisePropertyChanged("IsEnabled");
                InitializeGame();

                if (isReplay)
                {
                    Game.RewindGame();
                    ReplayGame();
                }
            });

            ActionCommand = new RelayCommand<TileViewModel>(tileViewModel =>
            {
                // If it is a selection.
                if (SelectedUnit == null && tileViewModel.HasUnit && Game.GetCurrentPlayer().Units.Contains(tileViewModel.Units[0].Unit))
                {
                    CleanReachablePositions();
                    CleanBestMoves();
                    SelectedUnit = tileViewModel.Units[0];

                    // Sets the new reachable positions.
                    var positions = SelectedUnit.Unit.FindReachablePositions(Game.Map);
                    // Selected unit Position
                    var sP = SelectedUnit.Unit.Position;
                    foreach (var position in positions)
                    {
                        
                        var index = position.X * Game.Map.Size + position.Y;
                        bool canAttack = sP.IsClose(position);
                        var ennemy = Game.GetCurrentPlayer() == Game.Players[0] ? Game.Players[1] : Game.Players[0];
                        var ennemyHere = false;
                        foreach (var u in ennemy.Units)
                        {
                            if (!u.IsDead()&&u.Position.Equals(position))
                                ennemyHere = true;
                        }
                       
                        if ((canAttack && ennemyHere) || !ennemyHere)
                        {
                            Tiles[index].IsReachable = true;
                        }
                        else
                        {
                            Tiles[index].IsReachable = false;
                        }
                       
                        Tiles[index].RaisePropertyChanged("IsReachable");

                    }
                    MoveManager moveManager = new MoveManager(Game);
                    IList<Position> bestMoves = moveManager.GetBestMove(Game.GetCurrentPlayer(),SelectedUnit.Unit);
                    foreach (var position in bestMoves)
                    {
                        var index = position.X * Game.Map.Size + position.Y;
                        Tiles[index].IsBestMove = true;
                        Tiles[index].RaisePropertyChanged("IsBestMove");
                        
                    }
                }
                // If it is an action.
                else if (SelectedUnit != null && tileViewModel.IsReachable)
                {
                    var index = Tiles.IndexOf(tileViewModel);

                    // Attack action.
                    if (tileViewModel.HasUnit && !Game.GetCurrentPlayer().Units.Contains(tileViewModel.Units[0].Unit))
                    {
                        // Selects the best defender.
                        var defender = tileViewModel.Units[0];
                        for (var i = 1; i < tileViewModel.Units.Count; i++)
                            if (tileViewModel.Units[i].Unit.GetRealDefensePoints() > defender.Unit.GetRealDefensePoints())
                                defender = tileViewModel.Units[i];
                        var attack = new AttackAction(Game.Map, SelectedUnit.Unit, defender.Unit);
                        Game.DoAction(attack);
                        
                        var loser = attack.Loser;
                        var winnerViewModel = loser == SelectedUnit.Unit ? defender : SelectedUnit; 
                        if (loser.IsDead())
                        {
                            foreach (var t in Tiles)
                            {
                                for (int i = 0; i < t.Units.Count; i++)
                                {
                                    if (t.Units[i].Unit == loser)
                                    {
                                        t.Units.RemoveAt(i);
                                        t.Units.Add(winnerViewModel);
                                    }
                                    else if (t.Units[i].Unit == winnerViewModel.Unit )
                                    {
                                        t.Units.RemoveAt(i);
                                    }
                                }
                                t.RaisePropertyChanged("Units");
                            }

                            

                            
                        }

                        foreach (var unitViewModel in Player1.Units)
                        {
                            unitViewModel.RaisePropertyChanged("");
                        }
                        foreach (var unitViewModel in Player2.Units)
                        {
                            unitViewModel.RaisePropertyChanged("");
                        }

                    }
                    // Move action.
                    else
                    {
                        var mapSize = Game.Map.Size;
                        var posDest = new Position(index/mapSize, index%mapSize);


                        var path = SelectedUnit.Unit.GetWay(Game.Map, posDest);
                        Game.DoAction(new MoveAction(Game.Map, SelectedUnit.Unit,posDest));
                        SelectedUnit.RaisePropertyChanged("Unit");
                        AnimateUnitMove(SelectedUnit,path);


                    }

                    SelectedUnit = null;
                    CleanReachablePositions();
                    CleanBestMoves();
                }
                else
                {
                    CleanReachablePositions();
                    CleanBestMoves();
                    SelectedUnit = null;
                }
            });

            NextTurnCommand = new RelayCommand(async () =>
            {
                if (Game.NextPlayer())
                {
                    RaisePropertyChanged("Game");
                    Player1.RaisePropertyChanged("Player");
                    Player2.RaisePropertyChanged("Player");
                    Messenger.Default.Send(new CurrentPlayerMessage(Game.GetCurrentPlayer()));
                }
                else
                {
                    var dialog = new Views.Dialogs.WinnerDialog();
                    dialog.DataContext = new Dialogs.WinnerDialogViewModel(Game.GetWinner().Name);

                    var path = (string) await DialogHost.Show(dialog, "RootDialog");
                    if (path != null)
                    {
                        ;
                    }

                    ExitGame();
                }
            });

            SaveGameDialogCommand = new RelayCommand(async () =>
            {
                var dialog = new Views.Dialogs.SaveGameDialog();

                var path = (string) await DialogHost.Show(dialog, "RootDialog");
                if (path != null)
                {
                    var directoryPath = $"{Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location)}/Saves";
                    if (!Directory.Exists(directoryPath))
                        Directory.CreateDirectory(directoryPath);
                    Game.SaveGame($"{directoryPath}/{path}.o");
                }
            });

            LoadGameDialogCommand = new RelayCommand(async () =>
            {
                var dialog = new Views.Dialogs.LoadGameDialog();

                var path = (string) await DialogHost.Show(dialog, "RootDialog");
                if (path != null)
                {
                    Game = Game.LoadGame(path);
                    InitializeGame();
                }
            });

            ExitGameDialogCommand = new RelayCommand(async () =>
            {
                if (!isReplay)
                {
                    var dialog = new Views.Dialogs.ConfirmDialog();

                    if (await DialogHost.Show(dialog, "RootDialog") != null)
                        ExitGame();
                }
                else
                    ExitGame();
            });
        }

        private void CleanReachablePositions()
        {
            // Removes all reachable positions.
            foreach (var tile in Tiles)
                if (tile.IsReachable)
                {
                    tile.IsReachable = false;
                    tile.RaisePropertyChanged("IsReachable");
                }
        }
        private void CleanBestMoves()
        {
            // Removes all reachable positions.
            foreach (var tile in Tiles)
                if (tile.IsBestMove)
                {
                    tile.IsBestMove = false;
                    tile.RaisePropertyChanged("IsBestMove");
                }
        }

        private void ExitGame()
        {
            Messenger.Default.Send(new NavigationViewMessage(new HomeViewModel()));
            Messenger.Default.Send(new NavigationSubviewMessage(new MainMenuViewModel()));
        }

        private void InitializeGame()
        {
            Player1 = new PlayerViewModel(Game.Players[0]);
            Player2 = new PlayerViewModel(Game.Players[1]);

            Tiles = new List<TileViewModel>(Game.Map.Size * Game.Map.Size);
            for (int x = 0; x < Game.Map.Size; x++)
                for (int y = 0; y < Game.Map.Size; y++)
                    Tiles.Add(new TileViewModel(Game.Map.GetTile(new Position(x, y))));

            RaisePropertyChanged("Game");
            RaisePropertyChanged("Tiles");
            Player1.RaisePropertyChanged("Player");
            Player2.RaisePropertyChanged("Player");

            // Initialize the game view
            Messenger.Default.Send(new CurrentPlayerMessage(Game.GetCurrentPlayer()));

            foreach (var unit in Player1.Units)
            {
                var position = unit.Unit.Position;
                Tiles[position.X * Game.Map.Size + position.Y].Units.Add(unit);
            }
            foreach (var unit in Player2.Units)
            {
                var position = unit.Unit.Position;
                Tiles[position.X * Game.Map.Size + position.Y].Units.Add(unit);
            }
        }


        public void ReplayGame()

        {
            rebuildTileViewModel();
            var d = Dispatcher.CurrentDispatcher;
            new Thread(new ThreadStart(() =>
            {
                foreach (var action  in Game.History)
                {

                    foreach (var p in Game.Players)
                    {
                        foreach (var pUnit in p.Units)
                        {
                            pUnit.MovePoints = Unit.MAX_MOVE_POINTS;
                        }
                    }
                    action.Do();
                    d.Invoke(() => rebuildTileViewModel());
                    Thread.Sleep(500);
                }
            })).Start();
        }

        private void rebuildTileViewModel()
        {
            RaisePropertyChanged("Game");
            foreach (var tileViewModel in Tiles)
            {
                for (var i = 0; i < tileViewModel.Units.Count; i++)
                {
                    var UnitViewModel = tileViewModel.Units[i];
                    tileViewModel.Units.RemoveAt(i);
                    var index = UnitViewModel.Unit.Position.X*Game.Map.Size + UnitViewModel.Unit.Position.Y;
                    Tiles[index].Units.Add(UnitViewModel);
                    Tiles[index].RaisePropertyChanged("Units");
                }
            }
            RaisePropertyChanged("Tiles");
        }

        private void AnimateUnitMove(UnitViewModel unit,IList<Position> path)
        {
            Dispatcher d = Dispatcher.CurrentDispatcher;
            new Thread(new ThreadStart(() =>
            {
                var initPos = path[0];
                var oldIndex = initPos.X * Game.Map.Size + initPos.Y;
                foreach (Position p in path)
                {
                   
                    d.Invoke(() =>
                    {
                        var newIndex = p.X*Game.Map.Size +p.Y;

                        Tiles[oldIndex].Units.Remove(unit);
                        Tiles[newIndex].Units.Add(unit);
                        Tiles[oldIndex].RaisePropertyChanged("Units");
                        Tiles[newIndex].RaisePropertyChanged("Units");
                        oldIndex = newIndex;

                    });
                    Thread.Sleep(100);

                }
            })).Start();

        }
    }
}