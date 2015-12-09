package Simulation;

public enum MockTeam
{
    A {
        public MockTeam opponent()
        {
            return MockTeam.B;
        }
    },
    B {
        public MockTeam opponent()
        {
            return MockTeam.A;
        }
    },
    Neutral {
        public MockTeam opponent()
        {
            return MockTeam.Neutral;
        }
    }
}
