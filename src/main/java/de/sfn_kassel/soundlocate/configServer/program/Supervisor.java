package de.sfn_kassel.soundlocate.configServer.program;

import de.sfn_kassel.soundlocate.configServer.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by robin on 31.03.16.
 * this is the Thread, that takes care about calling the ProcessDiedListener
 */
public class Supervisor extends Thread {
    private final ProcessDiedListener pd;
    private final List<Process> processes;
    private AtomicBoolean operation = new AtomicBoolean(false);

    @Override
    public synchronized void start() {
        super.start();
    }

    public Supervisor(ProcessDiedListener pd) {
        this.pd = pd;
        this.processes = new ArrayList<>();
        this.start();
    }

    public void addProcess(Process p) {
        synchronized (processes) {
            processes.add(p);
        }
    }

    public void removeProcess(Process p) {
        operation.set(true);
        synchronized (processes) {
            processes.remove(p);
        }
        operation.set(false);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (processes) {
                for (Process p: processes) {
                    if (!p.isAlive()) {
                        if (operation.get()) {
                            break;
                        }
                        pd.onProcessDied(p);
                        break;
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.log(e);
            }
        }
    }
}
