package org.tengel.splay;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

enum TimerState {STOPPED, RUNNING, ELAPSED}

public class SleepTimer
{
    int                         m_sleepTime = 0; // in seconds
    TimerState                  m_state = TimerState.STOPPED;
    ScheduledThreadPoolExecutor m_executor;


    class SleepTimerTask implements Runnable
    {
        @Override
        public void run()
        {
            tick();
        }
    }


    public void start()
    {
        m_sleepTime = (m_sleepTime + 600) % 3600;
        if (m_state != TimerState.RUNNING)
        {
            m_state = TimerState.RUNNING;
            m_executor = new ScheduledThreadPoolExecutor(1);
            m_executor.scheduleAtFixedRate(new SleepTimerTask(), 0, 1,
                                           TimeUnit.SECONDS);
        }
    }


    private void tick()
    {
        if (m_state == TimerState.RUNNING && m_sleepTime > 0)
        {
            m_sleepTime -= 1;
        }
        if (m_state == TimerState.RUNNING && m_sleepTime == 0)
        {
            m_state = TimerState.ELAPSED;
            m_executor.shutdown();
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
        return Util.msToStr(m_sleepTime * 1000);
    }
}
