#ifndef _TOKENBUCKET_H_
#define _TOKENBUCKET_H_

#include <mutex>
#include <condition_variable>
#include <chrono>
#include <thread>

class tokenbucket
{
	using mutex = typename std::mutex;
	using condition_variable = typename std::condition_variable;
	using thread = typename std::thread;
	using high_clock = typename std::chrono::high_resolution_clock;
	using miliseconds = typename std::chrono::milliseconds;
	using lock_guard = typename std::lock_guard<typename mutex>;
	using unique_lock = typename std::unique_lock<typename mutex>;
public:
	static tokenbucket& getInstance();
	//获取令牌
	bool acquire();
	//非阻塞
	bool nonblockacquire();
	//permitsPerSecond:每秒钟可以执行的最大请求数
	void setPermitsPerSecond(size_t permitsPerSecond);
	void quit();
	//maxtoken:令牌桶中的最大令牌个数
	void setMaxToken(size_t maxtoken);
	void beginWork();
	void stopWork();	
protected:    
	tokenbucket();
	~tokenbucket();
private:
	bool quit_;
	bool stop_;	
	size_t millseconds_;//产生令牌的周期，单位为ms
	size_t maxtoken_;//最大令牌个数
	size_t token_;
	mutex mut_;
	condition_variable cv_;
	thread thr;
};



#endif
