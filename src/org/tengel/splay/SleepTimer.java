package org.tengel.splay;

enum TimerState {STOPPED, RUNNING, ELAPSED}

public class SleepTimer
{
    int        m_sleepTime = 0;
    TimerState m_state = TimerState.STOPPED;

    public void start()
    {
        m_sleepTime = (m_sleepTime + 6000) % 36000;
        if (m_state == TimerState.RUNNING)
        {
            return;
        }
        m_state = TimerState.RUNNING;
    }

    public void tick()
    {
        if (m_state == TimerState.RUNNING && m_sleepTime > 0)
        {
            m_sleepTime -= 1;
        }
        if (m_state == TimerState.RUNNING && m_sleepTime == 0)
        {
            m_state = TimerState.ELAPSED;
        }
    }

    public boolean isElapsed()
    {
        if (m_state == TimerState.ELAPSED)
        {
            m_state = TimerState.STOPPED;
            return true;
        }
        return false;
    }

    public String getTimeLeft()
    {
        return Util.msToStr(m_sleepTime * 100);
    }
}
