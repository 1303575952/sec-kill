#include "TokenBucket.h"
#include <iostream>
#include <mutex>
#include <chrono>
using std::cin;
using std::cout;
using std::thread;
using high_clock = typename std::chrono::high_resolution_clock;
using miliseconds = typename std::chrono::milliseconds;
using nanoseconds = typename std::chrono::nanoseconds;
using seconds = typename std::chrono::seconds;
using mutex=typename std::mutex;
using lock_guard = typename std::lock_guard<typename mutex>;
using std::chrono::duration_cast;
mutex mut;

//总共使用的令牌数
size_t count = 0;

static const int a = [ ] {
	std::ios::sync_with_stdio(false);
	cin.tie(nullptr);
	cout.tie(nullptr);
	return 0;
}();
void thr1(bool& stop_,size_t nanosecond)
{
	tokenbucket& token = tokenbucket::getInstance();
	thread::id current_id = std::this_thread::get_id();
	seconds cur_time;
	while (!stop_)
	{
		std::this_thread::sleep_for(nanoseconds(nanosecond));
		if (token.acquire())
		{
			lock_guard lock_(mut);
			++count;
			cur_time = duration_cast<seconds>(high_clock::now().time_since_epoch());
			cout << current_id << ":acquire token in " << cur_time.count() << "\n";
		}
		else
		{			
			lock_guard lock_(mut);
			cur_time = duration_cast<seconds>(high_clock::now().time_since_epoch());
			cout << current_id << ":don't acquire token in " << high_clock::now().time_since_epoch().count() << "\n";
		}
	}
	lock_guard lock_(mut);
	cout << current_id << ":quit\n";
}
void thr2(bool& stop_, size_t nanosecond)
{
	tokenbucket& token = tokenbucket::getInstance();
	thread::id current_id = std::this_thread::get_id();
	seconds cur_time;
	while (!stop_)
	{
		std::this_thread::sleep_for(nanoseconds(nanosecond));
		if (token.nonblockacquire())
		{
			lock_guard lock_(mut);
			++count;
			cur_time = duration_cast<seconds>(high_clock::now().time_since_epoch());
			cout << current_id << ":acquire token in " << cur_time.count() << "\n";
		}
		else
		{			
			lock_guard lock_(mut);
			cur_time = duration_cast<seconds>(high_clock::now().time_since_epoch());
			cout << current_id << ":don't acquire token in " << cur_time.count() << "\n";
		}
	}
	lock_guard lock_(mut);
	cout << current_id << ":quit\n";
}
int main(void)
{
	bool stop_ = false;
	auto& token = tokenbucket::getInstance();
	cout << "max token:" << token.getMaxToken() << "\n";

	auto start = high_clock::now();

	//每3ns采用阻塞方式请求一次令牌
	thread t1(thr1,std::ref(stop_),3);

	{
		lock_guard lock_(mut);
		cout << "set max token:50\n";
		token.setMaxToken(100);
		cout << "max token:" << token.getMaxToken() << "\n";
		cout << "permits per second:" << token.getnumpersecond() << "\n";
	}

	//每2ns采用非阻塞方式请求一次令牌
	thread t2(thr2, std::ref(stop_), 2);

	{
		lock_guard lock_(mut);
		cout << "set permits per second:100\n";
		token.setPermitsPerSecond(1000000);
		cout << "permits per second:" << token.getnumpersecond() << "\n";
	}

	//每5ns采用阻塞方式请求一次令牌
	thread t3(thr1, std::ref(stop_), 5);
	//等待1s
	std::this_thread::sleep_for(miliseconds(1000));
	stop_ = true;
	token.quit();	
	auto end = high_clock::now();

	{
		lock_guard lock_(mut);
		cout << "\n运行时间：" << duration_cast<miliseconds>(end - start).count() << "ms使用令牌数：" << count << "\n";
	}
	
	t1.join();
	t2.join();
	t3.join();	
	cout << "main thread quit\n";
	return 0;
}