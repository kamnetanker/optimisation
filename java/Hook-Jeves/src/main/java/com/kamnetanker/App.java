package com.kamnetanker;


import java.util.function.Function;
@FunctionalInterface
interface Log{
    public void log(String descr, double[] dot, double step, double epsilon, Function<double[], Double> Fl);
}

public class App
{
    static double r=1;

    static double F(double[] x)
    {
        return 4*x[0]*x[0]-4*x[0]*x[1]+3*x[1]*x[1]+x[0];
    }
    static double P(double[] x)
    {
        return x[0]+x[1]+2;
    }
    static double Phi(double[] x)
    {
        return F(x)+r*P(x);
    }
//==============================================
    static boolean Equals(double arg, double brg){
        return Math.abs(arg-brg)<0.000001;
    }
    static String DoubleArrToString(double[] arg){
        String s = "";
        for(int i=0; i<arg.length; i++){
            s+=((Double)arg[i]).toString()+" ";
        }
        return s.replace('.', ',');
    }
    static void Logger(String descr, double[] dot, double step, double epsilon, Function<double[], Double> Fl){
        String array_s = DoubleArrToString(dot);
        String step_s =((Double)step).toString().replace('.',',');
        String epsilon_s = ((Double)epsilon).toString().replace('.',',');
        String func_v_s = Fl.apply(dot).toString().replace('.',',');
        System.out.printf("%s %s\t%s\t%s\t%s\n", descr,array_s, step_s, epsilon_s, func_v_s);
    }
    static void Logger2(String descr, double[] dot, double step, double epsilon, Function<double[], Double> Fl){
        String array_s = DoubleArrToString(dot);
        String step_s =((Double)step).toString().replace('.',',');
        String epsilon_s = ((Double)epsilon).toString().replace('.',',');
        String func_v_s = Fl.apply(dot).toString().replace('.',',');
        System.out.printf("%.2f %s %s\t%s\t%s\t%s\n", r,descr,array_s, step_s, epsilon_s, func_v_s);
    }
    static void Logger3(String descr, double[] dot, double step, double epsilon, Function<double[], Double> Fl){
        System.out.printf("");
    }
    static double[] CopyArray(double[] x)
    {
        double[] new_arr = new double[x.length];

        for (int i = 0; i < x.length; i++)
        {
            new_arr[i] = x[i];
        }

        return new_arr;
    }
    static double Compare(double[] x, double[] y)
    {
        double comp_ret=x.length-y.length;

        for(int i=0; i<x.length&&i<y.length&&comp_ret==0; i++)
        {
            comp_ret=x[i]-y[i];
        }

        return comp_ret;
    }
    static double[] VectorSum(double[] x, double[] y)
    {
        double[] sum = CopyArray(x);
        for(int i=0; i<sum.length&&i<y.length;i++)
        {
            sum[i]+=y[i];
        }
        return sum;
    }
    static double[] VectorMultiplex(double[] vec, double arg) {
        double[] sum = CopyArray(vec);

        for (int i = 0; i < sum.length; i++) {
            sum[i] *= arg;
        }
        return sum;
    }
//=====================================================================================
    static double[] SearchOne(double[] x,double step, Function<double[], Double> Fl)
    {
        double tmpX[] = CopyArray(x);
        double Fx = Fl.apply(x), Fxa = 0;

        for(int i=0; i<tmpX.length; i++)
        {

            tmpX[i]+=step;
            Fxa = Fl.apply(tmpX);

            if(Fx<Fxa)
            {
                tmpX[i]-=2*step;
            }

            Fxa = Fl.apply(tmpX);

            if(Fx<Fxa)
            {
                tmpX[i]+=step;
            }
        }
        return tmpX;
    }
    static double[] SearchTwo(double[] x, double[] reference, double step, Function<double[], Double> Fl)
    {
        double tmpX[] = CopyArray(x);
        double Fx = Fl.apply(reference), Fxa=0;
        for(int i=0; i<tmpX.length; i++)
        {
            tmpX[i]+=step;
            Fxa = Fl.apply(x);

            if(Fx<Fxa)
            {
                tmpX[i]-=2*step;
            }

            Fxa = Fl.apply(tmpX);

            if(Fx<Fxa)
            {
                tmpX[i]+=step;
            }
        }
        return tmpX;
    }

    static double[] HookeJeves(double[] x, double step, double epsilon, Function<double[], Double> Fl, Log l)
    {
        double[][] T=new double[3][];
        T[0]=CopyArray(x);
        for(int i=1; i<T.length; i++)
        {
            T[i]=new double[x.length];
        }
        Integer stage_count=0, inner_stage_count=0;

        l.log("start:", T[0], step,epsilon,Fl);

        while(step>epsilon)
        {
            inner_stage_count=0;
            stage_count++;

            T[1]=SearchOne(T[0], step, Fl);

            double FDelta = Fl.apply(T[1])-Fl.apply(T[0]);

            if(Equals(FDelta, 0)){
                step/=2;
            }

            l.log(stage_count.toString()+"."+inner_stage_count.toString(),T[1], step,epsilon,Fl);

            while(FDelta<0)
            {

                T[2] = VectorSum(VectorMultiplex(T[1],2), VectorMultiplex(T[0], -1.0));
                T[2] = SearchTwo(T[2], T[1], step, Fl);

                double F1=Fl.apply(T[1]), F2=Fl.apply(T[2]);

                T[0]=CopyArray(T[1]);

                if(F1>F2)
                {
                    T[1]=CopyArray(T[2]);
                }
                FDelta = F2-F1;
                inner_stage_count++;

                l.log(stage_count.toString()+"."+inner_stage_count.toString(),T[1], step,epsilon,Fl);

            }
        }
        return T[0];
    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println("Legend");
        System.out.println("stage\tx1\tx2\tstep\tepsilon\tvalue");
        double[] startDot = new double[]{10.0, -10.0};
        double step = 1;
        double epsilon = 0.01;
        Function<double[], Double> Fl = App::F;
        Function<double[], Double> Pl = App::P;
        Function<double[], Double> Phil = App::Phi;
        System.out.println(DoubleArrToString(HookeJeves(startDot,step,epsilon,Fl, App::Logger)));


        System.out.println("Legend");
        r=10;
        System.out.println("r\tx1\tx2\tF\tPhi\tP");
        double[] x = HookeJeves(startDot,step,epsilon,Phil, App::Logger3);
        while(Math.abs(Pl.apply(x))>0.00001){
            x = HookeJeves(startDot,step,epsilon,Phil, App::Logger3);
            System.out.printf("%.6f\t%s\t%.5f\t%.5f\t%.5f\n",r,DoubleArrToString(x), Fl.apply(x), Phil.apply(x),Pl.apply(x));
            r+=Pl.apply(x);
        }
        r=-10;
        System.out.println("r\tx1\tx2\tF\tPhi\tP");
        x = HookeJeves(startDot,step,epsilon,Phil, App::Logger3);
        while(Math.abs(Pl.apply(x))>0.00001){
            x = HookeJeves(startDot,step,epsilon,Phil, App::Logger3);
            System.out.printf("%.6f\t%s\t%.5f\t%.5f\t%.5f\n",r,DoubleArrToString(x), Fl.apply(x), Phil.apply(x),Pl.apply(x));
            r+=Pl.apply(x);
        }

    }
}
