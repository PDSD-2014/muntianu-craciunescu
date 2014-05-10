class GetDataFromServerTask extends AsyncTask<Input, Result, Void> {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition tryAgain = lock.newCondition();
    private volatile boolean finished = false;

    @Override
    protected Void doInBackground(Input... params) {

        lock.lockInterruptibly();

        do { 
            // This is the bulk of our task, request the data, and put in "result"
            Result result = ....

            // Return it to the activity thread using publishProgress()
            publishProgress(result);

            // At the end, we acquire a lock that will delay
            // the next execution until runAgain() is called..
            tryAgain.await();

        } while(!finished);

        lock.unlock();
    }

    @Override
    protected void onProgressUpdate(Result... result) 
    {
        // Treat this like onPostExecute(), do something with result

        // This is an example...
        if (result != whatWeWant && userWantsToTryAgain()) {
            runAgain();
        }
    }

    public void runAgain() {
        // Call this to request data from the server again
        tryAgain.signal();
    }

    public void terminateTask() {
        // The task will only finish when we call this method
        finished = true;
        lock.unlock();
    }

    @Override
    protected void onCancelled() {
        // Make sure we clean up if the task is killed
        terminateTask();
    }
}
