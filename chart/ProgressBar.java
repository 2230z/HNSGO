package chart;

public class ProgressBar {

    /**
     * 当前进度
     */
    private int index;
    /**
     * 步长
     */
    private int step;
    /**
     * 进度条长度,总进度数值
     */
    private int barLength;

    /**
     * 是否初始化
     */
    private boolean hasInited = false;
    /**
     * 是否已经结束
     */
    private boolean hasFinished = false;
    /**
     * 进度条title
     */
    private String title;

    private static final char processChar = '█';
    private static final char waitChar = '─';


    private ProgressBar() {
        index = 0;
        step = 1;
        barLength = 100;
        title = "Progress:";
    }

    public static ProgressBar build() {
        return new ProgressBar();
    }

    public static ProgressBar build(int barLength) {
        ProgressBar progressBar = build();
        progressBar.barLength = barLength;
        return progressBar;
    }


    public static ProgressBar build(int index, int step) {
        ProgressBar progressBar = build(step);
        progressBar.index = index;
        return progressBar;
    }

    public static ProgressBar build(int index, int step, String title) {
        ProgressBar progressBar = build(index, step);
        progressBar.title = title;
        return progressBar;
    }

    private String generate(int num, char ch) {
        if (num == 0) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }

    private String genProcess(int num) {
        return generate(num, processChar);
    }

    private String genWaitProcess(int num) {
        return generate(num, waitChar);
    }

    /**
     * 清空进度条
     */
    private void cleanProcessBar() {
        System.out.print(generate(barLength / step + 6, '\b'));
    }

    /**
     * 进度+1
     */
    public void process() {
        checkStatus();
        checkInit();
        cleanProcessBar();
        index++;
        drawProgressBar();
        checkFinish();
    }

    /**
     * 进度+指定数值
     *
     * @param process 指定数值
     */
    public void process(int process) {
        checkStatus();
        checkInit();
        cleanProcessBar();
        if (process >= barLength)
            index = barLength;
        else
            index = process;
        drawProgressBar();
        checkFinish();
    }

    /**
     * 步进
     */
    public void step() {
        checkStatus();
        checkInit();
        cleanProcessBar();
        if (index + step >= barLength)
            index = barLength;
        else
            index += step;
        drawProgressBar();
        checkFinish();
    }


    /**
     * 绘制进度条
     */
    public void drawProgressBar() {
        System.out.print(
                String.format(
                        "├%s%s┤%3d%%",
                        genProcess(index / step),
                        genWaitProcess(barLength / step - index / step),
                        index                        
                )
        );
    }


    /**
     * 检查进度条状态
     * 已完成的进度条不可以继续执行
     */
    private void checkStatus() {
        // if (hasFinished) throw XFSException.illegalStateException("进度条已经完成");
    }

    /**
     * 检查是否已经初始化
     */
    private void checkInit() {
        if (!hasInited) init();
    }


    /**
     * 检查是否已经完成
     */
    private void checkFinish() {
        if (hasFinished() && !hasFinished) finish();
    }

    /**
     * 是否已经完成进度条
     *
     * @return
     */
    public boolean hasFinished() {
        return index >= barLength;
    }

    /**
     * 初始化进度条
     */
    public void init() {
        checkStatus();
        System.out.print(title);
        System.out.print(String.format("[%s%s]%3d%%", genProcess(index / step), genWaitProcess(barLength / step - index / step),index));
        hasInited = true;
    }

    /**
     * 结束进度条，由 checkFinish()调用
     */
    private void finish() {
        System.out.println();
        hasFinished = true;
    }

    /**
     * 间隔50ms 自动执行进度条
     *
     * @throws InterruptedException
     */
    public void printProgress() throws InterruptedException {
        init();
        do {
            step();
            Thread.sleep(50);
            index++;
        } while (index <= barLength);
        System.out.println();
    }

    public void run() {
        this.init();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
        this.step();

    }

    public static void main(String[] args) throws InterruptedException {
        ProgressBar progressBar = ProgressBar.build();
        progressBar.init();
        int i = 1;
        int N = 100;
        int process = 0;
        double pre = 0d;
        while(i <= N) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                process = (int)((i*1.0) / N * 100);
                if(process > pre){
                    pre = process;
                    progressBar.process(process);
                }
                i++;
            }
        }
    }
}
