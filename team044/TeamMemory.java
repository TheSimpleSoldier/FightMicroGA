package team044;

/**
 * Define the purpose of the 32 team memory channels and explain how to use them
 * Created by David on 1/10/2015.
 */
public enum TeamMemory
{
    PreviousStrategy,   // Not implemented
    EnemyUnitBuild,
    AttackTiming,       // Round when first building takes damage. Least 12 bits contain round number, next 4 bits contain most frequent unit, last bits contain second most frequent unit
    HQHP,
    harassDrone,
    EnemyHarrass,
    TowerHP,
    TowersUp,
    TimeLeft,
    // etc
}
