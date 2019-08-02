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
	using microseconds = typename std::chrono::microseconds;
	using nanoseconds = typename std::chrono::nanoseconds;
	using lock_guard = typename std::lock_guard<typename mutex>;
	using unique_lock = typename std::unique_lock<typename mutex>;
public:
	tokenbucket(const tokenbucket&) = delete;
	tokenbucket operator=(const tokenbucket&)=delete;
	static tokenbucket& getInstance();
	//��ȡ����
	bool acquire();
	//������
	bool nonblockacquire();
	//permitsPerSecond:ÿ�����������Ƶĸ���
	void setPermitsPerSecond(size_t permitsPerSecond);
	void quit();
	//maxtoken:����Ͱ�е�������Ƹ���
	void setMaxToken(size_t maxtoken);
	size_t getMaxToken()const;
	size_t getnumpersecond();
	void beginWork();
	void stopWork();	
protected:    
	tokenbucket();
	~tokenbucket();
private:
	bool quit_;
	bool stop_;	
	size_t nanoseconds_;//�������Ƶ����ڣ���λΪms
	size_t maxtoken_;//������Ƹ���
	size_t token_;
	mutex mut_;
	condition_variable cv_;
	thread thr;
};



#endif
