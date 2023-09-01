#include <bits/stdc++.h>
using namespace std;
const int N=510,inf=1e18;
typedef long long ll;
ll a[N][N],sum[N][N],dp[N];
int main()
{
    ios::sync_with_stdio(false);
    int n;cin>>n;
    for(int i=1;i<=n;i++)
    {
        for(int j=1;j<=n;j++)
        {
            cin>>a[i][j];
            sum[i][j]=sum[i-1][j]+a[i][j]; // 第j列前i行的前缀和
        }
    }
    ll mx=-inf;
    for(int k1=1;k1<=n;k1++) // 行 上端点
    {
        for(int k2=k1;k2<=n;k2++) // 行 下端点
        {
            //dp[0]=0;
            for(int i=1;i<=n;i++) // 列
            {
                ll tmp=sum[k2][i]-sum[k1-1][i]; // 第i列 [k1,k2]行之和
                dp[i]=max(dp[i-1]+tmp,tmp); // 最大子段和求法
                mx=max(mx,dp[i]);
            }
        }
    }
    printf("%lld\n",mx);
    return 0;
}
/*
3
-1 -4 3
3 4 -1
-5 -2 8
ans:10

4
0 -2 -7 0
9 2 -6 2
-4 1 -4  1
-1 8  0 -2
ans:15
*/
