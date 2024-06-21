# Konum Paylaşım Odaklı Etkinlik Daveti Mobil Uygulaması
Konum paylaşım odaklı etkinlik davet sosyal medya uygulaması, kullanıcıların konum tabanlı etkinliklerini paylaşabilecekleri, keşfedebilecekleri ve katılabilecekleri bir mobil uygulamadır. Uygulama, kullanıcıların sosyal etkileşimlerini artırmayı, yeni insanlarla tanışmalarını ve eğlenceli deneyimler yaşamalarını hedefler.

## Proje Amacı
Projenin amacı, insanların konum tabanlı etkinlikleri paylaşabilecekleri, keşfedebilecekleri ve katılabilecekleri bir platform oluşturmaktır. Bu platform, kullanıcıların sosyal etkileşimde bulunmalarını sağlar, yeni insanlarla tanışmalarını teşvik eder ve eğlenceli deneyimler yaşamalarını sağlar. Ayrıca, kullanıcıların kendi kişilik özelliklerine uygun etkinliklere katılmalarını sağlayan bir kişilik testi gibi özelliklerle kullanıcı deneyimini zenginleştirir.

## Proje İçeriği
Proje, bir mobil uygulama üzerinden gerçekleştirilecek. Bu uygulama, kullanıcıların konum tabanlı etkinliklerini paylaşmalarını, diğer etkinliklere katılmalarını ve konumlarına yakın olan etkinlikleri keşfetmelerini sağlayacak. Kullanıcılar aynı zamanda etkinlikler hakkında sohbet edebilecekleri ve iletişim kurabilecekleri özel sohbet odalarına erişebilecekler. Uygulama, kullanıcıların kişilik özelliklerini değerlendirecekleri bir Big 5 kişilik testi sunacak ve bu test sonuçlarına göre kullanıcıların kendi kişiliklerine uygun etkinliklere katılmasını sağlayacak. Bu sayede kullanıcılar, benzer kişilik özelliklerine sahip insanlarla daha uyumlu etkinliklere katılma şansına sahip olacaklar. Uygulama, kullanıcıların sosyal etkileşimlerini artırmayı, yeni bağlantılar kurmalarını ve eğlenceli deneyimler yaşamalarını hedefliyor.

## Ana Özellikler:
  1.	Etkinlik Paylaşımı: Kullanıcılar bu uygulama ile kendi etkinliklerini oluşturabilir ve bunları diğer kullanıcılarla paylaşabilirler. Ayrıca bu uygulama katılımcılara yönelik konum, zaman, etkinlik açıklamaları ve bilgileri içerir.
  2.	Etkinlik Arama: Bu uygulamayla kullanıcılar konumlarının olduğu alandaki yakınındaki etkinlikleri harita üzerinde görebilir. Etkinlikleri tarihe, kategoriye veya popülerliğe göre sıralamak için filtre ayarlarını da kullanabilirler. Bu şekilde kolaylık sağlarlar.
  3.	Özel Sohbetler: Her etkinlik için özel bir sohbet oluşturulur. Kullanıcılar isterlerse sohbetlere katılabilirler. Bu odalarda katılımcılar etkinlik hakkında iletişim kurabilir, bilgi alışverişinde bulunabilir ve planlarını koordine edebilir ve eğlenebilirler.
  4.	Big 5 Kişilik Testi: Uygulamanın diğer bir özelliği ise, kullanıcılar Big 5 Kişilik Testini kullanarak kişilik özelliklerini değerlendirebilirler. Test sonuçlarına göre isterlerse kullanıcılara önerilen işlemler sunulur ve seçebilirler.
  5.	Kullanıcı profili: Uygulamada kullanıcılar kendi kişisel profillerini oluşturabilir ve ilgi alanlarını, etkinlik geçmişlerini ve sosyal ağlarını paylaşabilirler.
     
## Teknik Detaylar:
  1.	Platform: Android Studio kullanılarak Java diliyle geliştirilmiştir.
  2.	Konum Hizmetleri: Google Maps API entegrasyonu ile konum tabanlı özellikler sağlanacak. (Not: Bu api güvenlik nedenlerinden dolayı kullanıma kapatıldı. Eğer uygulamayı kullanmak istiyorsanız cloud.google.com üzerinden bir key alıp, AndroidManifest.xml dosyasına key'i girmeniz gerekmektedir.)
  3.	Veritabanı: Kullanıcı bilgileri ve etkinlik verileri için firebase veritabanı kullanılmıştır.
  4.	Java: Android uygulamaları için temel programlama dili.
  5.	Firebase Authentication: Kullanıcı kimlik doğrulaması için Firebase Authentication kullanılmıştır.
  6.	XML: Android kullanıcı arayüzü tasarımı için XML dilini kullanılmıştır.
  7.	Android SDK
  8.	Makine Öğrenmesi: Geliştirilen model sayesinde kullanıcıların 50 soruya verecekleri 1-5 arası puanlamaya göre bir kişilik analizi çıkartılacaktır. Bu, kullanıcıların profillerinde görünmesini sağlayacaktır.
  9.	API: Geliştirilen makine öğrenmesi modeline erişim için bir api yazıldı. Bu api kullanılarak modelden cevap alınmaktadır. (Not: API proje geliştirilmesi bittikten sonra güvenlik nedenlerinden dolayı kullanıma kapatıldığı için uygulamayı kullancığınızda erişemeyeceksiniz.)

## Kullanım Videosu
https://github.com/SemihGul5/KonumUygulamasi/assets/133046330/fefccd4b-f8ff-4f6a-898e-c5d36da4593d

